/// [TransactionResponse] receive after user complete or cancels transaction
class TransactionResponse {
  /// Transaction status of payment
  late String transactionStatus;

  /// Amount paid by customer
  late double amountPaid;

  /// Payable amount for the transaction
  late double amountPayable;

  /// Payment reference for the transaction
  late String paymentReference;

  /// Transaction reference of the transaction
  late String transactionReference;

  /// Currency code of the transaction
  late String currencyCode;

  /// Payment method used by the customer
  late String paymentMethod;

  /// Payment data
  late String paymentDate;

  TransactionResponse() {
    transactionStatus = '';
    amountPaid = 0;
    amountPayable = 0;
    paymentReference = '';
    transactionReference = '';
    currencyCode = '';
    paymentMethod = '';
    paymentDate = '';
  }

  /// Converts [Map] to [TransactionResponse]
  factory TransactionResponse.fromMap(Map<String, dynamic> map) {
    return TransactionResponse()
      ..transactionStatus = map['transactionStatus'] ?? ''
      ..amountPaid = map['amountPaid'] ?? 0
      ..amountPayable = map['amountPayable'] ?? 0
      ..paymentReference = map['paymentReference'] ?? ''
      ..transactionReference = map['transactionReference'] ?? ''
      ..currencyCode = map['currencyCode'] ?? ''
      ..paymentMethod = map['paymentMethod'] ?? ''
      ..paymentDate = map['paymentDate'] ?? '';
  }

  /// Converts [TransactionResponse] to [Map]
  Map<String, dynamic> toMap() {
    final Map<String, dynamic> map = {};

    map['transactionStatus'] = transactionStatus;
    map['amountPaid'] = amountPaid;
    map['amountPayable'] = amountPayable;
    map['paymentReference'] = paymentReference;
    map['transactionReference'] = transactionReference;
    map['currencyCode'] = currencyCode;
    map['paymentMethod'] = paymentMethod;
    map['paymentDate'] = paymentDate;

    return map;
  }

  @override
  String toString() {
    return 'TransactionResponse{transactionStatus: $transactionStatus, amountPaid: $amountPaid, amountPayable: $amountPayable, paymentReference: $paymentReference, transactionReference: $transactionReference, currencyCode: $currencyCode, paymentMethod: $paymentMethod, paymentDate: $paymentDate}';
  }
}
