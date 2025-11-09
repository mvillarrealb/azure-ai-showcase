Esto es el flujo que yo espero:
Cliente EMP001 solicita crédito por 4000 soles;

1. Busqueda transaccional en base de datos:
	SELECT c.identity_document,c.monthly_income, c.current_Debt, eh.*
	FROM customers c
	LEFT JOIN LATERAL (
		SELECT start_date, end_date, income
		FROM employment_history
		WHERE customer_id = c.id
		ORDER BY end_date DESC 
		LIMIT 2
	)eh ON TRUE
	wHERE identity_document = 'EMP001'

El resultado:

identity_document|montly_income|current_debt|start_date|end_date|income
---|---|---|---|---|---
"EMP001"	|5500.00	|1000.00	|"2023-01-01"||5500.00
"EMP001"	|5500.00	|1000.00	|"2020-01-15"|"2022-12-31"|3800.00

Como leo esto:
- El cliente tiene un ingreso mensual de 5500 soles y una deuda actual de 1000 soles.
- Entre su ultimo empleo y el actual no ha tenido interrupciones (no hay gap entre end_date y start_date)

2. Busqueda semantica en AI Search:

Con el resultadoa armariamos un string para buscar en AI Search:

Cliente con ingresos de 5500 soles y deuda actual de 1000 soles. No ha tenido interrupciones laborales entre su ultimo empleo y el actual.

Esto debería darnos basicamente a que Rank pertenece el cliente

3. Con el Rank Obtenido y el criterio original (solicitud de 4000 soles) armamos otra busqueda semantica para el documento de productos:

Cliente con Rank ORO solicita un crédito de 4000 soles.

Esto ya devolvería las condiciones del crédito para ese cliente.

Notas importantes:

1. El gap de empleo se calcula en base a los 2 ultimos empleos, se supone que el empleo actual no tiene end_date, se calcula el tiempo entre el end_date del penultimo empleo y el start_date del ultimo empleo. si el gap es menor a 30 dias se considera sin interrupciones, si es mayor a 30 dias deberíamos contar en meses la cantidad de meses de interrupción.

2. Si el cliente no tiene historial de empleo (no hay registros en employment_history) se asume que no ha tenido empleos previos.

