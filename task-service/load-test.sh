#!/bin/bash
# ==========================================
# Test obciążeniowy usługi operacyjnej
# ==========================================

BASE_URL="http://localhost:8080/api/tasks"
TOTAL_REQUESTS=100
CONCURRENT=10

echo "=== TEST OBCIĄŻENIOWY USŁUGI OPERACYJNEJ ==="
echo "Żądania: $TOTAL_REQUESTS | Współbieżność: $CONCURRENT"
echo ""

# Test 1: Tworzenie zadań
echo "--- Test 1: POST (tworzenie zadań) ---"
seq 1 $TOTAL_REQUESTS | xargs -P $CONCURRENT -I {} \
    curl -s -o /dev/null -w "%{http_code} %{time_total}s\n" \
    -X POST "$BASE_URL" \
    -H "Content-Type: application/json" \
    -d "{\"title\":\"Test zadanie {}\",\"priority\":\"MEDIUM\"}" \
    | tee /tmp/post_results.txt

echo ""
echo "Podsumowanie POST:"
echo "  Sukces (201): $(grep -c "^201" /tmp/post_results.txt)"
echo "  Błędy:        $(grep -cv "^201" /tmp/post_results.txt)"
echo "  Śr. czas:     $(awk '{sum+=$2; n++} END {printf "%.3fs", sum/n}' /tmp/post_results.txt)"

# Test 2: Odczyt listy
echo ""
echo "--- Test 2: GET (pobieranie listy) ---"
seq 1 $TOTAL_REQUESTS | xargs -P $CONCURRENT -I {} \
    curl -s -o /dev/null -w "%{http_code} %{time_total}s\n" \
    "$BASE_URL" \
    | tee /tmp/get_results.txt

echo ""
echo "Podsumowanie GET:"
echo "  Sukces (200): $(grep -c "^200" /tmp/get_results.txt)"
echo "  Błędy:        $(grep -cv "^200" /tmp/get_results.txt)"
echo "  Śr. czas:     $(awk '{sum+=$2; n++} END {printf "%.3fs", sum/n}' /tmp/get_results.txt)"

echo ""
echo "=== TEST ZAKOŃCZONY ==="