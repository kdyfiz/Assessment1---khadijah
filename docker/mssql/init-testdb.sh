#!/bin/bash
set -e

HOST="${MSSQL_HOST:-localhost}"
PASS="${MSSQL_SA_PASSWORD:-YourStrong@Passw0rd}"
SQLCMD="/opt/mssql-tools18/bin/sqlcmd"

echo "Waiting for MSSQL at ${HOST}:1433 ..."
for i in $(seq 1 60); do
  if ${SQLCMD} -S "${HOST}" -U sa -P "${PASS}" -C -Q "SELECT 1" &>/dev/null; then
    echo "MSSQL is ready."
    break
  fi
  echo "  attempt ${i}/60 ..."
  sleep 2
done

echo "Creating database TESTDB (if not exists)..."
${SQLCMD} -S "${HOST}" -U sa -P "${PASS}" -C -Q "
IF DB_ID('TESTDB') IS NULL
BEGIN
  CREATE DATABASE TESTDB;
  PRINT 'TESTDB created';
END
ELSE
BEGIN
  PRINT 'TESTDB already exists';
END
"

# Optional seed (ignore errors if table not ready yet — JPA will create tables on app start)
if [ -f /schema.sql ]; then
  echo "Applying schema/seed (best-effort)..."
  ${SQLCMD} -S "${HOST}" -U sa -P "${PASS}" -C -d TESTDB -i /schema.sql || true
fi

echo "MSSQL init complete. TESTDB is ready."
