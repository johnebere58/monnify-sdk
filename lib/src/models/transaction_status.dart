// ignore_for_file: constant_identifier_names

/// [TransactionStatus] of a transaction made by the customer
enum TransactionStatus {
  PAID,
  OVERPAID,
  PARTIALLY_PAID,
  FAILED,
  PENDING,
  CANCELLED,
  PAYMENT_GATEWAY_ERROR,
}
