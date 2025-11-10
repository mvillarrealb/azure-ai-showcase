
# Introducción práctica a Azure AI Services con Python SDK**

## **Objetivo general**

Aprender a interactuar con los principales servicios de Azure AI desde Python utilizando los SDKs oficiales, a través de ejemplos simples y reproducibles:

1. **Azure AI Document Intelligence** – extracción de texto desde un documento.
2. **Azure AI Search** – creación e indexación básica de datos y ejecución de una búsqueda semántica.
3. **Azure OpenAI** – invocación de un modelo GPT para generación de texto.
4. **Azure AI Foundry** – orquestación y conexión entre los servicios previos en un flujo simple.

---

## **Pre-requisitos**

* Cuenta activa de Azure y permisos para crear recursos.
* Python 3.9+ instalado.
* Extensión de Azure CLI `az cognitiveservices` y `az openai`.
* Variables de entorno configuradas con claves y endpoints:

```bash
export AZURE_DOCUMENT_INTELLIGENCE_ENDPOINT="https://<your-doc-intelligence-endpoint>.cognitiveservices.azure.com/"
export AZURE_DOCUMENT_INTELLIGENCE_KEY="<your-key>"

export AZURE_SEARCH_ENDPOINT="https://<your-search-endpoint>.search.windows.net"
export AZURE_SEARCH_KEY="<your-key>"

export AZURE_OPENAI_ENDPOINT="https://<your-openai-endpoint>.openai.azure.com/"
export AZURE_OPENAI_KEY="<your-key>"
```

---

# **Módulo 1: Azure AI Document Intelligence**

### **Propósito**

Extraer texto y estructura de un documento usando el modelo `prebuilt-layout`.

### **Instalación del SDK**

```bash
pip install azure-ai-documentintelligence
```

### **Ejemplo mínimo**

```python
from azure.ai.documentintelligence import DocumentIntelligenceClient
from azure.core.credentials import AzureKeyCredential

endpoint = os.getenv("AZURE_DOCUMENT_INTELLIGENCE_ENDPOINT")
key = os.getenv("AZURE_DOCUMENT_INTELLIGENCE_KEY")

client = DocumentIntelligenceClient(endpoint, AzureKeyCredential(key))

with open("sample-invoice.pdf", "rb") as f:
    poller = client.begin_analyze_document("prebuilt-layout", document=f)
    result = poller.result()

for page in result.pages:
    print(f"Page {page.page_number} - text lines:")
    for line in page.lines:
        print(line.content)
```

### **Resultado esperado**

Texto extraído del documento, línea por línea, directamente en consola.

---

# **Módulo 2: Azure AI Search**

### **Propósito**

Crear un índice básico y realizar una búsqueda semántica simple.

### **Instalación del SDK**

```bash
pip install azure-search-documents
```

### **Ejemplo mínimo**

```python
from azure.search.documents import SearchClient
from azure.core.credentials import AzureKeyCredential

endpoint = os.getenv("AZURE_SEARCH_ENDPOINT")
key = os.getenv("AZURE_SEARCH_KEY")

index_name = "demo-index"

client = SearchClient(endpoint=endpoint, index_name=index_name, credential=AzureKeyCredential(key))

# Consulta semántica simple
results = client.search("manual de políticas de seguridad")

for result in results:
    print(result)
```

### **Resultado esperado**

Listado de documentos relevantes en la búsqueda, retornados en orden de relevancia semántica.

---

# **Módulo 3: Azure OpenAI**

### **Propósito**

Generar texto con un modelo GPT desplegado en Azure.

### **Instalación del SDK**

```bash
pip install openai
```

### **Configuración del cliente**

```python
import os
from openai import AzureOpenAI

client = AzureOpenAI(
    azure_endpoint=os.getenv("AZURE_OPENAI_ENDPOINT"),
    api_key=os.getenv("AZURE_OPENAI_KEY"),
    api_version="2024-02-01"
)

response = client.chat.completions.create(
    model="gpt-4o-mini",
    messages=[
        {"role": "system", "content": "Eres un asistente útil."},
        {"role": "user", "content": "Dime en una frase qué es Azure AI Services."}
    ]
)

print(response.choices[0].message.content)
```

### **Resultado esperado**

Una respuesta generada por GPT describiendo Azure AI Services en lenguaje natural.

---

# **Módulo 4: Azure AI Foundry**

### **Propósito**

Orquestar servicios combinados desde un solo flujo (ejemplo mínimo: conectar AI Search + OpenAI).

### **Concepto**

Foundry permite crear agentes o flujos de IA visualmente, pero también puedes emular su comportamiento con Python conectando servicios.

### **Ejemplo mínimo**

```python
from openai import AzureOpenAI
from azure.search.documents import SearchClient
from azure.core.credentials import AzureKeyCredential
import os

# Configuración
openai_client = AzureOpenAI(
    azure_endpoint=os.getenv("AZURE_OPENAI_ENDPOINT"),
    api_key=os.getenv("AZURE_OPENAI_KEY"),
    api_version="2024-02-01"
)

search_client = SearchClient(
    endpoint=os.getenv("AZURE_SEARCH_ENDPOINT"),
    index_name="demo-index",
    credential=AzureKeyCredential(os.getenv("AZURE_SEARCH_KEY"))
)

# Paso 1: Buscar documentos relevantes
query = "¿Cuáles son las políticas de seguridad de datos?"
results = search_client.search(query)
context = "\n".join([r["content"] for r in results])

# Paso 2: Usar OpenAI para generar una respuesta contextual
prompt = f"Basado en los siguientes documentos, responde en lenguaje natural:\n{context}"

response = openai_client.chat.completions.create(
    model="gpt-4o-mini",
    messages=[{"role": "user", "content": prompt}]
)

print(response.choices[0].message.content)
```

### **Resultado esperado**

Una respuesta redactada por GPT usando información encontrada en AI Search.
Este patrón es la base de un flujo **RAG (Retrieval-Augmented Generation)** y representa el tipo de integración que Azure AI Foundry orquesta visualmente.

---

# **Conclusión del Workshop**

* **Document Intelligence** extrae información estructurada de documentos.
* **AI Search** permite indexar y consultar información semántica.
* **OpenAI** genera texto y respuestas en lenguaje natural.
* **Foundry** unifica todo en un entorno visual y operativo para crear agentes y flujos de IA empresariales.

---

# **Anexo: Referencias oficiales**

| Servicio                           | Documentación oficial                                                                                                                                                             | SDK / Ejemplos                                                                                                                                                       |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Azure AI Document Intelligence** | [https://learn.microsoft.com/azure/ai-services/document-intelligence/](https://learn.microsoft.com/azure/ai-services/document-intelligence/)                                      | [https://github.com/Azure/azure-sdk-for-python/tree/main/sdk/documentintelligence](https://github.com/Azure/azure-sdk-for-python/tree/main/sdk/documentintelligence) |
| **Azure AI Search**                | [https://learn.microsoft.com/azure/search/](https://learn.microsoft.com/azure/search/)                                                                                            | [https://github.com/Azure/azure-sdk-for-python/tree/main/sdk/search](https://github.com/Azure/azure-sdk-for-python/tree/main/sdk/search)                             |
| **Azure OpenAI Service**           | [https://learn.microsoft.com/en-us/azure/ai-services/openai/overview](https://learn.microsoft.com/en-us/azure/ai-services/openai/overview) ([Microsoft Learn][1])                 | [https://github.com/openai/openai-python](https://github.com/openai/openai-python)                                                                                   |
| **Azure AI Foundry**               | [https://learn.microsoft.com/en-us/azure/ai-foundry/what-is-azure-ai-foundry](https://learn.microsoft.com/en-us/azure/ai-foundry/what-is-azure-ai-foundry) ([Microsoft Learn][2]) | [https://github.com/Azure-Samples/get-started-with-ai-agents](https://github.org/Azure-Samples/get-started-with-ai-agents)                                           |

