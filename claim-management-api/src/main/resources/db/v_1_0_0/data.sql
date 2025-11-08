
INSERT INTO claims (
    id, date, amount, identity_document, description, reason, sub_reason, status, comments, created_at, updated_at
) VALUES
('CLM-2024-001234', '2024-11-08 10:30:00+00', 1500.75, '12345678', 
 'Cargo no autorizado en mi tarjeta de crédito por compra que no realicé en establecimiento comercial. No reconozco la transacción y solicito la reversión inmediata.', 
 'Cargo indebido', 'Transacción no autorizada', 'open', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('CLM-2024-001235', '2024-11-07 14:15:00+00', 2500.00, '87654321', 
 'Error en el cálculo de intereses de mi préstamo personal. Los intereses aplicados no corresponden a la tasa acordada en el contrato firmado.', 
 'Error en cálculo', 'Intereses incorrectos', 'inProgress', 
 'Se está revisando con el área de créditos. Expediente enviado para validación de tasa contractual.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('CLM-2024-001236', '2024-11-06 09:45:00+00', 850.25, '11223344', 
 'No se aplicó el descuento prometido en mi tarjeta de crédito según la promoción vigente anunciada en su página web oficial.', 
 'Descuento no aplicado', 'Promoción no reflejada', 'resolved', 
 'Reclamo resuelto satisfactoriamente: Se aplicó el descuento correspondiente y se reembolsó la diferencia al cliente.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('CLM-2024-001237', '2024-11-05 16:20:00+00', 125.50, '55667788', 
 'Se me cobró una comisión por mantenimiento de cuenta corriente cuando según mi plan no debería aplicar este cargo.', 
 'Comisión incorrecta', 'Mantenimiento de cuenta', 'open', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('CLM-2024-001238', '2024-11-04 11:10:00+00', 5000.00, '99887766', 
 'Transferencia interbancaria que fue debitada de mi cuenta pero nunca llegó a destino. Han pasado más de 48 horas hábiles.', 
 'Transferencia fallida', 'Dinero no llega a destino', 'inProgress', 
 'Caso escalado al área de operaciones. Se está rastreando la transacción con el banco receptor.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;