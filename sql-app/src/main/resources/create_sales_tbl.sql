CREATE TABLE sales (
    purchase_uuid TEXT NOT NULL PRIMARY KEY,
    customer_id BYTEA,
    full_name BYTEA,
    email_address BYTEA,
    purchase_date DATE,
    product_id VARCHAR,
    product_category VARCHAR,
    quantity INTEGER,
    price_per_unit DECIMAL(10,2),
    total_price DECIMAL(10,2)
)