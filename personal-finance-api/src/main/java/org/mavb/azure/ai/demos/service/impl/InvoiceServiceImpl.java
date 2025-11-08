package org.mavb.azure.ai.demos.service.impl;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.models.*;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.SyncPoller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.response.InvoiceAnalysisDto;
import org.mavb.azure.ai.demos.dto.response.InvoiceLineItemDto;
import org.mavb.azure.ai.demos.exception.ProcessingException;
import org.mavb.azure.ai.demos.exception.ValidationException;
import org.mavb.azure.ai.demos.service.InvoiceService;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final String[] ALLOWED_CONTENT_TYPES = {"application/pdf", "image/jpeg", "image/png", "image/tiff"};
    private static final String MODEL_ID = "prebuilt-invoice";
    private final DocumentIntelligenceClient documentIntelligenceClient;

    @Override
    public InvoiceAnalysisDto scanInvoice(MultipartFile file) {
        log.debug("Procesando factura PDF: {}", file.getOriginalFilename());
        
        validateFile(file);
        
        try {
            byte[] fileBytes = file.getBytes();
            
            log.debug("Enviando documento a Azure Document Intelligence con modelo: {}", MODEL_ID);

            String base64Content = java.util.Base64.getEncoder().encodeToString(fileBytes);
            String jsonRequestBody = String.format("{\"base64Source\":\"%s\"}", base64Content);
            BinaryData analyzeRequest = BinaryData.fromString(jsonRequestBody);

            SyncPoller<BinaryData, BinaryData> poller = documentIntelligenceClient
                .beginAnalyzeDocument(MODEL_ID, analyzeRequest, new RequestOptions());

            BinaryData result = poller.getFinalResult();
            AnalyzeResult analyzeResult = result.toObject(AnalyzeResult.class);
            InvoiceAnalysisDto invoiceDto = mapToInvoiceAnalysisDto(analyzeResult);

            log.info("Factura procesada exitosamente: {} - Vendor: {} - Total: {}",
                invoiceDto.getInvoiceNumber(),
                invoiceDto.getVendor(),
                invoiceDto.getTotalAmount());

            return invoiceDto;

        } catch (IOException e) {
            log.error("Error leyendo el archivo: {}", e.getMessage(), e);
            throw new ProcessingException("Error al leer el archivo de factura", e);
        } catch (Exception e) {
            log.error("Error procesando factura con Azure Document Intelligence: {}", e.getMessage(), e);
            throw new ProcessingException("Error al procesar la factura: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Mono<InvoiceAnalysisDto> scanInvoiceFromFilePart(FilePart filePart) {
        log.debug("Procesando factura PDF desde FilePart: {}", filePart.filename());
        
        try {
            validateFilePart(filePart);
        } catch (ValidationException e) {
            return Mono.error(e);
        }
        
        return Mono.fromCallable(() -> Files.createTempFile("invoice_", ".pdf"))
                .flatMap(tempFile -> 
                    filePart
                            .transferTo(tempFile)
                        .then(Mono.fromCallable(() -> {
                            try {
                                byte[] fileBytes = Files.readAllBytes(tempFile);
                                Files.delete(tempFile);
                                return fileBytes;
                            } catch (IOException e) {
                                throw new RuntimeException("Error leyendo archivo temporal", e);
                            }
                        }))
                )
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(fileBytes -> Mono.fromCallable(() -> {
                    try {
                        String base64Content = java.util.Base64.getEncoder().encodeToString(fileBytes);
                        String jsonRequestBody = String.format("{\"base64Source\":\"%s\"}", base64Content);
                        BinaryData analyzeRequest = BinaryData.fromString(jsonRequestBody);

                        SyncPoller<BinaryData, BinaryData> poller = documentIntelligenceClient
                            .beginAnalyzeDocument(MODEL_ID, analyzeRequest, new RequestOptions());

                        BinaryData result = poller.getFinalResult();
                        AnalyzeResult analyzeResult = result.toObject(AnalyzeResult.class);
                        InvoiceAnalysisDto invoiceDto = mapToInvoiceAnalysisDto(analyzeResult);

                        log.info("Factura procesada exitosamente desde FilePart: {} - Vendor: {} - Total: {}",
                            invoiceDto.getInvoiceNumber(),
                            invoiceDto.getVendor(),
                            invoiceDto.getTotalAmount());

                        return invoiceDto;

                    } catch (Exception e) {
                        log.error("Error procesando factura con Azure Document Intelligence: {}", e.getMessage(), e);
                        throw new ProcessingException("Error al procesar la factura: " + e.getMessage(), e);
                    }
                }).subscribeOn(Schedulers.boundedElastic()))
                .onErrorMap(RuntimeException.class, e -> {
                    if (e.getCause() instanceof IOException) {
                        return new ProcessingException("Error al leer el archivo de factura", e.getCause());
                    }
                    return e;
                });
    }
    
    /**
     * Mapea el resultado de Azure Document Intelligence a nuestro DTO
     */
    private InvoiceAnalysisDto mapToInvoiceAnalysisDto(AnalyzeResult analyzeResult) {
        if (analyzeResult.getDocuments() == null || analyzeResult.getDocuments().isEmpty()) {
            throw new ProcessingException("No se pudo extraer información de la factura");
        }

        AnalyzedDocument document = analyzeResult.getDocuments().getFirst();
        Map<String, DocumentField> fields = document.getFields();

        log.debug("Campos extraídos del documento: {}", fields != null ? fields.keySet() : "ninguno");

        String invoiceNumber = extractStringField(fields, "InvoiceId");
        LocalDateTime invoiceDate = extractDateField(fields, "InvoiceDate");
        BigDecimal totalAmount = extractAmountField(fields, "InvoiceTotal");
        String vendor = extractVendorName(fields);
        List<InvoiceLineItemDto> lineItems = extractLineItems(fields);

        return InvoiceAnalysisDto.builder()
            .invoiceNumber(invoiceNumber)
            .date(invoiceDate)
            .totalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO)
            .vendor(vendor)
            .lineItems(lineItems)
            .build();
    }

    /**
     * Extrae el nombre del vendedor de los campos del documento
     */
    private String extractVendorName(Map<String, DocumentField> fields) {
        String vendorName = extractStringField(fields, "VendorName");
        if (vendorName != null && !vendorName.isEmpty()) {
            return vendorName;
        }

        DocumentField vendorAddress = fields != null ? fields.get("VendorAddress") : null;
        if (vendorAddress != null && vendorAddress.getValueString() != null) {
            return vendorAddress.getValueString();
        }

        return "Vendedor desconocido";
    }

    /**
     * Extrae los items de línea de la factura
     */
    private List<InvoiceLineItemDto> extractLineItems(Map<String, DocumentField> fields) {
        List<InvoiceLineItemDto> lineItems = new ArrayList<>();

        if (fields == null) {
            return lineItems;
        }

        DocumentField itemsField = fields.get("Items");
        if (itemsField == null || itemsField.getType() != DocumentFieldType.ARRAY) {
            log.warn("No se encontraron items en la factura");
            return lineItems;
        }

        List<DocumentField> items = itemsField.getValueList();
        if (items == null) {
            return lineItems;
        }

        log.debug("Procesando {} items de la factura", items.size());

        for (DocumentField item : items) {
            if (item.getType() == DocumentFieldType.OBJECT) {
                Map<String, DocumentField> itemFields = item.getValueMap();

                InvoiceLineItemDto lineItem = InvoiceLineItemDto.builder()
                    .description(extractStringField(itemFields, "Description"))
                    .quantity(extractQuantityField(itemFields, "Quantity"))
                    .unitPrice(extractAmountField(itemFields, "UnitPrice"))
                    .totalPrice(extractAmountField(itemFields, "Amount"))
                    .build();

                lineItems.add(lineItem);
            }
        }

        return lineItems;
    }

    /**
     * Extrae un campo de texto del mapa de campos
     */
    private String extractStringField(Map<String, DocumentField> fields, String fieldName) {
        if (fields == null) {
            return null;
        }

        DocumentField field = fields.get(fieldName);
        if (field != null) {
            String value = field.getValueString();
            if (value != null) {
                log.debug("Campo {} extraído: {}", fieldName, value);
                return value;
            }
        }

        log.debug("Campo {} no encontrado o vacío", fieldName);
        return null;
    }

    /**
     * Extrae un campo de fecha del mapa de campos
     */
    private LocalDateTime extractDateField(Map<String, DocumentField> fields, String fieldName) {
        if (fields == null) {
            return null;
        }

        DocumentField field = fields.get(fieldName);
        if (field != null) {
            LocalDate dateValue = field.getValueDate();
            if (dateValue != null) {
                log.debug("Campo de fecha {} extraído: {}", fieldName, dateValue);
                return dateValue.atStartOfDay();
            }
        }

        log.debug("Campo de fecha {} no encontrado", fieldName);
        return null;
    }

    /**
     * Extrae un campo de cantidad del mapa de campos
     */
    private BigDecimal extractQuantityField(Map<String, DocumentField> fields, String fieldName) {
        if (fields == null) {
            return BigDecimal.ONE;
        }

        DocumentField field = fields.get(fieldName);
        if (field != null) {
            Double value = field.getValueNumber();
            if (value != null) {
                return BigDecimal.valueOf(value);
            }
        }

        return BigDecimal.ONE;
    }

    /**
     * Extrae un campo de monto/moneda del mapa de campos
     */
    private BigDecimal extractAmountField(Map<String, DocumentField> fields, String fieldName) {
        if (fields == null) {
            return null;
        }

        DocumentField field = fields.get(fieldName);
        if (field != null) {
            if (field.getType() == DocumentFieldType.CURRENCY) {
                CurrencyValue currencyValue = field.getValueCurrency();
                if (currencyValue != null) {
                    double amount = currencyValue.getAmount();
                    String code = currencyValue.getCurrencyCode();
                    log.debug("Campo de monto {} extraído: {} {}", fieldName, amount, code);
                    return BigDecimal.valueOf(amount);
                }
            }

            if (field.getType() == DocumentFieldType.NUMBER) {
                Double numberValue = field.getValueNumber();
                if (numberValue != null) {
                    log.debug("Campo numérico {} extraído: {}", fieldName, numberValue);
                    return BigDecimal.valueOf(numberValue);
                }
            }
        }

        log.debug("Campo de monto {} no encontrado", fieldName);
        return null;
    }

    /**
     * Valida el archivo de factura
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("El archivo es requerido");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("El archivo excede el tamaño máximo permitido de 10MB");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new ValidationException("Solo se permiten archivos PDF");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new ValidationException("El archivo debe tener extensión .pdf");
        }
    }
    
    /**
     * Valida el archivo de factura desde FilePart
     */
    private void validateFilePart(FilePart filePart) {
        if (filePart == null) {
            throw new ValidationException("El archivo es requerido");
        }
        
        String filename = filePart.filename();
        if (!filename.toLowerCase().endsWith(".pdf")) {
            throw new ValidationException("El archivo debe tener extensión .pdf");
        }
        
        String contentType = filePart.headers().getContentType() != null ? 
            filePart.headers().getContentType().toString() : null;
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new ValidationException("Solo se permiten archivos PDF");
        }
    }
    
    /**
     * Verifica si el tipo de contenido está permitido
     */
    private boolean isAllowedContentType(String contentType) {
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }
        return false;
    }
}