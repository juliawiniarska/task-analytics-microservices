#!/bin/bash
# ==========================================
# Test odporności na awarię
# ==========================================

echo "=== TEST ODPORNOŚCI NA AWARIĘ ==="
echo ""

# Test 1: Sprawdź czy usługa działa
echo "--- 1. Sprawdzenie stanu usługi ---"
curl -s http://localhost:8080/actuator/health
echo ""

# Test 2: Zatrzymaj RabbitMQ
echo "--- 2. Zatrzymanie RabbitMQ ---"
docker stop rabbitmq
echo "RabbitMQ zatrzymany."
echo ""

# Test 3: Sprawdź czy CRUD nadal działa
echo "--- 3. Test CRUD bez RabbitMQ ---"
echo "POST (tworzenie zadania):"
curl -s -w "\nHTTP Status: %{http_code}\n" \
    -X POST http://localhost:8080/api/tasks \
    -H "Content-Type: application/json" \
    -d '{"title":"Zadanie bez RabbitMQ","priority":"HIGH"}'
echo ""

echo "GET (pobieranie zadań):"
curl -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/api/tasks | head -c 200
echo ""

# Test 4: Sprawdź health
echo ""
echo "--- 4. Health check bez RabbitMQ ---"
curl -s http://localhost:8080/actuator/health
echo ""

# Test 5: Przywróć RabbitMQ
echo "--- 5. Przywrócenie RabbitMQ ---"
docker start rabbitmq
echo "RabbitMQ uruchomiony ponownie."
sleep 10

echo ""
echo "--- 6. Health check po przywróceniu ---"
curl -s http://localhost:8080/actuator/health

echo ""
echo "=== TEST ZAKOŃCZONY ==="