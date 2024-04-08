import 'package:monnify_payment_sdk/src/models/transaction_status.dart';

extension TransactionStatusX on TransactionStatus {
  TransactionStatus getTransactionStatus() {
    return TransactionStatus.PENDING;
  }
}
