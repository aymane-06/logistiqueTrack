#!/bin/bash

# LogiTrack Security Endpoints Test Script
# Tests all endpoints with proper authentication and authorization rules
# Based on SecurityConfig.java security filter chain

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Base URL
BASE_URL="http://localhost:8080"

# Counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Function to print colored output
print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
    ((PASSED_TESTS++))
    ((TOTAL_TESTS++))
}

print_failure() {
    echo -e "${RED}✗ $1${NC}"
    ((FAILED_TESTS++))
    ((TOTAL_TESTS++))
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Function to test endpoint
test_endpoint() {
    local METHOD=$1
    local ENDPOINT=$2
    local TOKEN=$3
    local EXPECTED_STATUS=$4
    local DESCRIPTION=$5
    local DATA=$6

    ((TOTAL_TESTS++))
    
    if [ -z "$DATA" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" -X "$METHOD" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $TOKEN" \
            "$BASE_URL$ENDPOINT")
    else
        RESPONSE=$(curl -s -w "\n%{http_code}" -X "$METHOD" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $TOKEN" \
            -d "$DATA" \
            "$BASE_URL$ENDPOINT")
    fi
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" -eq "$EXPECTED_STATUS" ]; then
        print_success "$DESCRIPTION (Expected: $EXPECTED_STATUS, Got: $HTTP_CODE)"
        return 0
    else
        print_failure "$DESCRIPTION (Expected: $EXPECTED_STATUS, Got: $HTTP_CODE)"
        if [ ! -z "$BODY" ]; then
            echo -e "${RED}  Response: $BODY${NC}"
        fi
        return 1
    fi
}

# Check if application is running
print_header "Checking Application Status"
if curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1; then
    print_success "Application is running"
else
    print_failure "Application is not running at $BASE_URL"
    echo "Please start the application first: ./mvnw spring-boot:run"
    exit 1
fi

# ============================================
# PHASE 1: USER REGISTRATION
# ============================================
print_header "PHASE 1: User Registration (Public Routes)"

# Register ADMIN
print_info "Registering ADMIN user..."
ADMIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d '{
        "name": "Test Admin",
        "email": "test.admin@logitrack.com",
        "passwordHash": "AdminPass123!",
        "role": "ADMIN"
    }')

ADMIN_ID=$(echo $ADMIN_RESPONSE | grep -o '"id":"[^"]*' | grep -o '[a-f0-9-]*$' | head -1)
if [ ! -z "$ADMIN_ID" ]; then
    print_success "ADMIN registered (ID: $ADMIN_ID)"
else
    print_info "ADMIN already exists or registration failed"
    ADMIN_ID="existing-admin-id"
fi

# Register WAREHOUSE_MANAGER
print_info "Registering WAREHOUSE_MANAGER user..."
WH_MANAGER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d '{
        "name": "Test Warehouse Manager",
        "email": "test.whmanager@logitrack.com",
        "passwordHash": "WHManager123!",
        "role": "WAREHOUSE_MANAGER"
    }')

WH_MANAGER_ID=$(echo $WH_MANAGER_RESPONSE | grep -o '"id":"[^"]*' | grep -o '[a-f0-9-]*$' | head -1)
if [ ! -z "$WH_MANAGER_ID" ]; then
    print_success "WAREHOUSE_MANAGER registered (ID: $WH_MANAGER_ID)"
else
    print_info "WAREHOUSE_MANAGER already exists or registration failed"
    WH_MANAGER_ID="existing-whmanager-id"
fi

# Register CLIENT
print_info "Registering CLIENT user..."
CLIENT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d '{
        "name": "Test Client",
        "email": "test.client@logitrack.com",
        "passwordHash": "ClientPass123!",
        "role": "CLIENT"
    }')

CLIENT_ID=$(echo $CLIENT_RESPONSE | grep -o '"id":"[^"]*' | grep -o '[a-f0-9-]*$' | head -1)
if [ ! -z "$CLIENT_ID" ]; then
    print_success "CLIENT registered (ID: $CLIENT_ID)"
else
    print_info "CLIENT already exists or registration failed"
    CLIENT_ID="existing-client-id"
fi

# ============================================
# PHASE 2: USER AUTHENTICATION
# ============================================
print_header "PHASE 2: User Authentication (Public Routes)"

# Login ADMIN
print_info "Logging in ADMIN..."
ADMIN_LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "email": "test.admin@logitrack.com",
        "password": "AdminPass123!"
    }')

ADMIN_TOKEN=$(echo $ADMIN_LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
if [ ! -z "$ADMIN_TOKEN" ]; then
    print_success "ADMIN logged in successfully"
else
    print_failure "ADMIN login failed"
    echo "Response: $ADMIN_LOGIN_RESPONSE"
    exit 1
fi

# Login WAREHOUSE_MANAGER
print_info "Logging in WAREHOUSE_MANAGER..."
WH_MANAGER_LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "email": "test.whmanager@logitrack.com",
        "password": "WHManager123!"
    }')

WH_MANAGER_TOKEN=$(echo $WH_MANAGER_LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
if [ ! -z "$WH_MANAGER_TOKEN" ]; then
    print_success "WAREHOUSE_MANAGER logged in successfully"
else
    print_failure "WAREHOUSE_MANAGER login failed"
    exit 1
fi

# Login CLIENT
print_info "Logging in CLIENT..."
CLIENT_LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "email": "test.client@logitrack.com",
        "password": "ClientPass123!"
    }')

CLIENT_TOKEN=$(echo $CLIENT_LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
if [ ! -z "$CLIENT_TOKEN" ]; then
    print_success "CLIENT logged in successfully"
else
    print_failure "CLIENT login failed"
    exit 1
fi

# ============================================
# PHASE 3: Test Failed Authentication
# ============================================
print_header "PHASE 3: Test Authentication Failures"

test_endpoint "POST" "/api/auth/login" "" "401" "Failed login with wrong credentials" \
    '{"email":"wrong@email.com","password":"WrongPass123!"}'

# ============================================
# PHASE 4: Test Authorization - No Token
# ============================================
print_header "PHASE 4: Test Unauthorized Access (No Token)"

test_endpoint "GET" "/api/warehouses/all" "" "401" "Access warehouses without token"
test_endpoint "GET" "/api/products/all" "" "401" "Access products without token"
test_endpoint "GET" "/api/suppliers/all" "" "401" "Access suppliers without token"

# ============================================
# PHASE 5: Test ADMIN-Only Routes
# ============================================
print_header "PHASE 5: Test ADMIN-Only Routes"

# Create Warehouse (ADMIN only - POST)
print_info "Creating warehouse with ADMIN..."
WAREHOUSE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/warehouses/initialize" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d "{
        \"name\": \"Test Warehouse\",
        \"location\": \"Test Location\",
        \"warehouseManagerId\": \"$WH_MANAGER_ID\",
        \"active\": true
    }")

WAREHOUSE_ID=$(echo $WAREHOUSE_RESPONSE | grep -o '"warehouseId":"[^"]*' | cut -d'"' -f4)
if [ ! -z "$WAREHOUSE_ID" ]; then
    print_success "ADMIN created warehouse (ID: $WAREHOUSE_ID)"
else
    print_info "Warehouse creation response: $WAREHOUSE_RESPONSE"
    WAREHOUSE_ID="00000000-0000-0000-0000-000000000001"
fi

# Test CLIENT cannot create warehouse (should fail with 403)
test_endpoint "POST" "/api/warehouses/initialize" "$CLIENT_TOKEN" "403" "CLIENT cannot create warehouse" \
    "{\"name\":\"Unauthorized Warehouse\",\"location\":\"Test\",\"warehouseManagerId\":\"$WH_MANAGER_ID\",\"active\":true}"

# Test WH_MANAGER cannot create warehouse (should fail with 403)
test_endpoint "POST" "/api/warehouses/initialize" "$WH_MANAGER_TOKEN" "403" "WAREHOUSE_MANAGER cannot create warehouse" \
    "{\"name\":\"Unauthorized Warehouse\",\"location\":\"Test\",\"warehouseManagerId\":\"$WH_MANAGER_ID\",\"active\":true}"

# Create Supplier (ADMIN only - POST)
print_info "Creating supplier with ADMIN..."
SUPPLIER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/suppliers/add" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d '{
        "name": "Test Supplier",
        "contactInfo": "test@supplier.com"
    }')

SUPPLIER_ID=$(echo $SUPPLIER_RESPONSE | grep -o '"supplierId":"[^"]*' | cut -d'"' -f4)
if [ ! -z "$SUPPLIER_ID" ]; then
    print_success "ADMIN created supplier (ID: $SUPPLIER_ID)"
else
    print_info "Supplier creation response: $SUPPLIER_RESPONSE"
    SUPPLIER_ID="00000000-0000-0000-0000-000000000002"
fi

# Test CLIENT cannot create supplier (should fail with 403)
test_endpoint "POST" "/api/suppliers/add" "$CLIENT_TOKEN" "403" "CLIENT cannot create supplier" \
    '{"name":"Unauthorized Supplier","contactInfo":"test@test.com"}'

# Create Product (ADMIN only - POST)
print_info "Creating product with ADMIN..."
PRODUCT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/products/add" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d '{
        "name": "Test Product",
        "category": "Electronics",
        "active": true
    }')

PRODUCT_ID=$(echo $PRODUCT_RESPONSE | grep -o '"productId":"[^"]*' | cut -d'"' -f4)
if [ ! -z "$PRODUCT_ID" ]; then
    print_success "ADMIN created product (ID: $PRODUCT_ID)"
else
    print_info "Product creation response: $PRODUCT_RESPONSE"
    PRODUCT_ID="00000000-0000-0000-0000-000000000003"
fi

# Test CLIENT cannot create product (should fail with 403)
test_endpoint "POST" "/api/products/add" "$CLIENT_TOKEN" "403" "CLIENT cannot create product" \
    '{"name":"Unauthorized Product","category":"Test","active":true}'

# Create Carrier (ADMIN only - POST)
print_info "Creating carrier with ADMIN..."
CARRIER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/carriers/add" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d '{
        "name": "Test Carrier",
        "contactInfo": "test@carrier.com",
        "maxDailyCapacity": 100,
        "currentDailyShipments": 0,
        "cutOffTime": "14:00:00",
        "active": true
    }')

CARRIER_ID=$(echo $CARRIER_RESPONSE | grep -o '"carrierId":"[^"]*' | cut -d'"' -f4)
if [ ! -z "$CARRIER_ID" ]; then
    print_success "ADMIN created carrier (ID: $CARRIER_ID)"
else
    print_info "Carrier creation response: $CARRIER_RESPONSE"
    CARRIER_ID="00000000-0000-0000-0000-000000000004"
fi

# Test CLIENT cannot create carrier (should fail with 403)
test_endpoint "POST" "/api/carriers/add" "$CLIENT_TOKEN" "403" "CLIENT cannot create carrier" \
    '{"name":"Unauthorized Carrier","contactInfo":"test@test.com","maxDailyCapacity":50,"active":true}'

# ============================================
# PHASE 6: Test ADMIN Routes (/api/admins/**)
# ============================================
print_header "PHASE 6: Test /api/admins/** Routes (ADMIN Only)"

# These routes should only be accessible by ADMIN
# Note: We'll test with a dummy PO ID since we may not have one yet

test_endpoint "PATCH" "/api/admins/purchaseOrder-status/update/00000000-0000-0000-0000-000000000999" "$CLIENT_TOKEN" "403" \
    "CLIENT cannot access /api/admins/** endpoints" \
    '{"status":"APPROVED"}'

test_endpoint "PATCH" "/api/admins/purchaseOrder-status/update/00000000-0000-0000-0000-000000000999" "$WH_MANAGER_TOKEN" "403" \
    "WAREHOUSE_MANAGER cannot access /api/admins/** endpoints" \
    '{"status":"APPROVED"}'

# ============================================
# PHASE 7: Test Purchase Order Routes
# ============================================
print_header "PHASE 7: Test Purchase Order Routes (ADMIN + WAREHOUSE_MANAGER)"

# Create Purchase Order with WAREHOUSE_MANAGER (should succeed)
print_info "Creating purchase order with WAREHOUSE_MANAGER..."
PO_RESPONSE=$(curl -s -X POST "$BASE_URL/api/purchase-orders/create" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $WH_MANAGER_TOKEN" \
    -d "{
        \"warehouseId\": \"$WAREHOUSE_ID\",
        \"supplierId\": \"$SUPPLIER_ID\",
        \"lines\": [{
            \"productId\": \"$PRODUCT_ID\",
            \"quantity\": 100,
            \"unitPrice\": 500.00
        }]
    }")

PO_ID=$(echo $PO_RESPONSE | grep -o '"purchaseOrderId":"[^"]*' | cut -d'"' -f4)
if [ ! -z "$PO_ID" ]; then
    print_success "WAREHOUSE_MANAGER created purchase order (ID: $PO_ID)"
else
    print_info "PO creation response: $PO_RESPONSE"
    PO_ID="00000000-0000-0000-0000-000000000005"
fi

# Test CLIENT cannot create purchase order (should fail with 403)
test_endpoint "POST" "/api/purchase-orders/create" "$CLIENT_TOKEN" "403" "CLIENT cannot create purchase order" \
    "{\"warehouseId\":\"$WAREHOUSE_ID\",\"supplierId\":\"$SUPPLIER_ID\",\"lines\":[{\"productId\":\"$PRODUCT_ID\",\"quantity\":10,\"unitPrice\":500}]}"

# Test ADMIN can create purchase order (should succeed)
test_endpoint "POST" "/api/purchase-orders/create" "$ADMIN_TOKEN" "200" "ADMIN can create purchase order" \
    "{\"warehouseId\":\"$WAREHOUSE_ID\",\"supplierId\":\"$SUPPLIER_ID\",\"lines\":[{\"productId\":\"$PRODUCT_ID\",\"quantity\":50,\"unitPrice\":500}]}"

# Test GET all purchase orders with different roles
test_endpoint "GET" "/api/purchase-orders/all" "$ADMIN_TOKEN" "200" "ADMIN can view all purchase orders"
test_endpoint "GET" "/api/purchase-orders/all" "$WH_MANAGER_TOKEN" "200" "WAREHOUSE_MANAGER can view all purchase orders"
test_endpoint "GET" "/api/purchase-orders/all" "$CLIENT_TOKEN" "403" "CLIENT cannot view purchase orders"

# ============================================
# PHASE 8: Test Sales Order Routes
# ============================================
print_header "PHASE 8: Test Sales Order Routes (CLIENT + WAREHOUSE_MANAGER)"

# Create Sales Order with CLIENT (should succeed - POST /api/sales-orders/**)
print_info "Creating sales order with CLIENT..."
SO_RESPONSE=$(curl -s -X POST "$BASE_URL/api/sales-orders/create" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $CLIENT_TOKEN" \
    -d "{
        \"clientId\": \"$CLIENT_ID\",
        \"warehouseId\": \"$WAREHOUSE_ID\",
        \"lines\": [{
            \"productId\": \"$PRODUCT_ID\",
            \"quantity\": 10,
            \"unitPrice\": 800.00,
            \"backorder\": false
        }]
    }")

SO_ID=$(echo $SO_RESPONSE | grep -o '"salesOrderId":"[^"]*' | cut -d'"' -f4)
if [ ! -z "$SO_ID" ]; then
    print_success "CLIENT created sales order (ID: $SO_ID)"
else
    print_info "SO creation response: $SO_RESPONSE"
    SO_ID="00000000-0000-0000-0000-000000000006"
fi

# Test WAREHOUSE_MANAGER cannot create sales order (should fail with 403)
test_endpoint "POST" "/api/sales-orders/create" "$WH_MANAGER_TOKEN" "403" "WAREHOUSE_MANAGER cannot create sales order" \
    "{\"clientId\":\"$CLIENT_ID\",\"warehouseId\":\"$WAREHOUSE_ID\",\"lines\":[{\"productId\":\"$PRODUCT_ID\",\"quantity\":5,\"unitPrice\":800,\"backorder\":false}]}"

# Test ADMIN cannot create sales order (should fail with 403)
test_endpoint "POST" "/api/sales-orders/create" "$ADMIN_TOKEN" "403" "ADMIN cannot create sales order" \
    "{\"clientId\":\"$CLIENT_ID\",\"warehouseId\":\"$WAREHOUSE_ID\",\"lines\":[{\"productId\":\"$PRODUCT_ID\",\"quantity\":5,\"unitPrice\":800,\"backorder\":false}]}"

# Test GET all sales orders
test_endpoint "GET" "/api/sales-orders/all" "$ADMIN_TOKEN" "200" "ADMIN can view all sales orders"
test_endpoint "GET" "/api/sales-orders/all" "$WH_MANAGER_TOKEN" "200" "WAREHOUSE_MANAGER can view all sales orders"
test_endpoint "GET" "/api/sales-orders/all" "$CLIENT_TOKEN" "403" "CLIENT cannot view all sales orders"

# ============================================
# PHASE 9: Test Sales Order Fulfillment Routes
# ============================================
print_header "PHASE 9: Test Sales Order Fulfillment (ADMIN + WAREHOUSE_MANAGER)"

# Test CLIENT cannot reserve (should fail with 403)
test_endpoint "PUT" "/api/sales-orders/$SO_ID/reserve" "$CLIENT_TOKEN" "403" "CLIENT cannot reserve sales order"

# Test CLIENT cannot ship (should fail with 403)
test_endpoint "PUT" "/api/sales-orders/$SO_ID/ship" "$CLIENT_TOKEN" "403" "CLIENT cannot ship sales order" \
    "{\"carrierId\":\"$CARRIER_ID\"}"

# Test CLIENT cannot deliver (should fail with 403)
test_endpoint "PUT" "/api/sales-orders/$SO_ID/deliver" "$CLIENT_TOKEN" "403" "CLIENT cannot deliver sales order"

# ============================================
# PHASE 10: Test GET Routes for Suppliers and Carriers
# ============================================
print_header "PHASE 10: Test GET Suppliers/Carriers Routes (ADMIN + WAREHOUSE_MANAGER)"

# Test GET suppliers
test_endpoint "GET" "/api/suppliers/all" "$ADMIN_TOKEN" "200" "ADMIN can view suppliers"
test_endpoint "GET" "/api/suppliers/all" "$WH_MANAGER_TOKEN" "200" "WAREHOUSE_MANAGER can view suppliers"
test_endpoint "GET" "/api/suppliers/all" "$CLIENT_TOKEN" "403" "CLIENT cannot view suppliers"

# Test GET carriers
test_endpoint "GET" "/api/carriers/all" "$ADMIN_TOKEN" "200" "ADMIN can view carriers"
test_endpoint "GET" "/api/carriers/all" "$WH_MANAGER_TOKEN" "200" "WAREHOUSE_MANAGER can view carriers"
test_endpoint "GET" "/api/carriers/all" "$CLIENT_TOKEN" "403" "CLIENT cannot view carriers"

# ============================================
# PHASE 11: Test UPDATE and DELETE Routes (ADMIN Only)
# ============================================
print_header "PHASE 11: Test UPDATE/DELETE Routes (ADMIN Only)"

# Test UPDATE product - CLIENT should fail
test_endpoint "PUT" "/api/products/update/$PRODUCT_ID" "$CLIENT_TOKEN" "403" "CLIENT cannot update product" \
    '{"name":"Updated Product","category":"Electronics","active":true}'

# Test UPDATE product - WAREHOUSE_MANAGER should fail
test_endpoint "PUT" "/api/products/update/$PRODUCT_ID" "$WH_MANAGER_TOKEN" "403" "WAREHOUSE_MANAGER cannot update product" \
    '{"name":"Updated Product","category":"Electronics","active":true}'

# Test DELETE product - CLIENT should fail
test_endpoint "DELETE" "/api/products/delete/$PRODUCT_ID" "$CLIENT_TOKEN" "403" "CLIENT cannot delete product"

# Test DELETE product - WAREHOUSE_MANAGER should fail
test_endpoint "DELETE" "/api/products/delete/$PRODUCT_ID" "$WH_MANAGER_TOKEN" "403" "WAREHOUSE_MANAGER cannot delete product"

# ============================================
# PHASE 12: Test Public Routes
# ============================================
print_header "PHASE 12: Test Public Routes"

test_endpoint "GET" "/" "" "200" "Access home route without authentication"

# ============================================
# SUMMARY
# ============================================
print_header "TEST SUMMARY"

echo -e "${BLUE}Total Tests: $TOTAL_TESTS${NC}"
echo -e "${GREEN}Passed: $PASSED_TESTS${NC}"
echo -e "${RED}Failed: $FAILED_TESTS${NC}"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "\n${GREEN}✓ All security tests passed!${NC}\n"
    exit 0
else
    echo -e "\n${RED}✗ Some tests failed. Please review the security configuration.${NC}\n"
    exit 1
fi
