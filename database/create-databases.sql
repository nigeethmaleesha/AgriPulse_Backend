\set ON_ERROR_STOP on

SELECT 'CREATE DATABASE agripulse_module1_dispatch'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'agripulse_module1_dispatch')\gexec

SELECT 'CREATE DATABASE agripulse_module2_allocation'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'agripulse_module2_allocation')\gexec

SELECT 'CREATE DATABASE agripulse_module3_network'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'agripulse_module3_network')\gexec

SELECT 'CREATE DATABASE agripulse_module4_spoilage'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'agripulse_module4_spoilage')\gexec

SELECT 'CREATE DATABASE agripulse_module5_scheduling'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'agripulse_module5_scheduling')\gexec
