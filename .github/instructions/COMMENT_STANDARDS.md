# Estándares de Comentarios para Credit Management API

## Reglas de Comentarios

### ✅ PERMITIDO - Solo JavaDoc mínimo y necesario:
```java
/**
 * Processes customer credit evaluation.
 */
@Service
public class EvaluationService {
    
    /**
     * Evaluates credit eligibility.
     * 
     * @param request Evaluation request
     * @return Evaluation response
     */
    public EvaluationResponse evaluate(EvaluationRequest request) {
        // TODO: implement complex logic
        return processEvaluation(request);
    }
}
```

### ❌ PROHIBIDO - Comentarios verbosos:
```java
/**
 * This service handles all the complex business logic for customer credit evaluation
 * including semantic risk assessment, product matching, and comprehensive analysis
 * of customer financial profiles using advanced algorithms and AI-powered insights.
 */

// Calculate basic credit metrics for the customer based on income and debt ratios
CreditMetrics metrics = calculateMetrics();

/**
 * Finds eligible products using traditional repository logic with advanced filtering
 * capabilities and comprehensive business rules validation.
 */
```

## Estándares:

1. **JavaDoc**: Solo para métodos públicos, una línea descriptiva + @param/@return
2. **Comentarios inline**: Solo para lógica compleja que no es obvia
3. **NO explicar lo obvio**: Si el código es self-explanatory, no comentar
4. **NO comentarios decorativos**: Evitar frases largas y explicaciones innecesarias
5. **TODO/FIXME**: Permitidos para trabajo pendiente

## Ejemplos correctos:
- `/** Evaluates credit eligibility. */`
- `// Handle edge case for negative amounts`
- `// TODO: add validation`

## Ejemplos incorrectos:
- Descripciones de 3+ líneas explicando lo obvio
- Comentarios que repiten el nombre del método
- Explicaciones detalladas de conceptos básicos