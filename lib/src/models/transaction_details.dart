import 'package:flutter/foundation.dart';
import 'package:monnify_payment_sdk/src/models/payment_method.dart';
import 'package:monnify_payment_sdk/src/models/sub_account_details.dart';

/// [TransactionDetails] used to initialize a payment
class TransactionDetails {
  /// amount to be paid
  late double amount;

  /// currency code for the payment
  late String currencyCode;

  /// name of customer
  late String customerName;

  /// The email address of the customer
  late String customerEmail;

  /// The payment reference for the transaction
  late String paymentReference;

  /// The description for the payment
  late String paymentDescription;

  /// customer metadata.
  late Map<String, String> metaData;

  /// selected payment methods
  late List<PaymentMethod> paymentMethods;

  /// income split config
  late List<SubAccountDetails> incomeSplitConfig;

  TransactionDetails({
    double? amount,
    String? currencyCode,
    String? customerName,
    String? customerEmail,
    String? paymentReference,
    String? paymentDescription,
    Map<String, String>? metaData,
    List<PaymentMethod>? paymentMethods,
    List<SubAccountDetails>? incomeSplitConfig,
  }) {
    this.amount = amount ?? 0;
    this.currencyCode = currencyCode ?? 'NGN';
    this.customerName = customerName ?? '';
    this.customerEmail = customerEmail ?? '';
    this.paymentReference = paymentReference ?? '';
    this.paymentDescription = paymentDescription ?? '';
    this.metaData = metaData ?? {};
    this.paymentMethods = paymentMethods ?? [];
    this.incomeSplitConfig = incomeSplitConfig ?? [];
  }

  /// creates new copy of [TransactionDetails] with updated values
  TransactionDetails copyWith(
      {double? amount,
      String? currencyCode,
      String? customerName,
      String? customerEmail,
      String? paymentReference,
      String? paymentDescription,
      Map<String, String>? metaData,
      List<PaymentMethod>? paymentMethods,
      List<SubAccountDetails>? incomeSplitConfig}) {
    return TransactionDetails()
      ..amount = amount ?? this.amount
      ..currencyCode = currencyCode ?? this.currencyCode
      ..customerName = customerName ?? this.customerName
      ..customerEmail = customerEmail ?? this.customerEmail
      ..paymentReference = paymentReference ?? this.paymentReference
      ..paymentDescription = paymentDescription ?? this.paymentDescription
      ..metaData = metaData ?? this.metaData
      ..paymentMethods = paymentMethods ?? this.paymentMethods
      ..incomeSplitConfig = incomeSplitConfig ?? this.incomeSplitConfig;
  }

  /// Converts [TransactionDetails] to map
  Map<String, dynamic> toMap() {
    var subAccountDetailsList = [];
    for (final subAccountDetails in incomeSplitConfig) {
      subAccountDetailsList.add(subAccountDetails.toMap());
    }

    var paymentMethodsList = [];
    for (final paymentMethod in paymentMethods) {
      paymentMethodsList.add(describeEnum(paymentMethod));
    }

    return {
      'amount': amount,
      'currencyCode': currencyCode,
      'customerName': customerName,
      'customerEmail': customerEmail,
      'paymentReference': paymentReference,
      'paymentDescription': paymentDescription,
      'metaData': metaData,
      'paymentMethods': paymentMethodsList,
      'incomeSplitConfig': subAccountDetailsList,
    };
  }

  @override
  String toString() {
    return 'TransactionDetails{amount: $amount, currencyCode: $currencyCode, customerName: $customerName, customerEmail: $customerEmail, paymentReference: $paymentReference, paymentDescription: $paymentDescription, metaData: $metaData, paymentMethods: $paymentMethods, incomeSplitConfig: $incomeSplitConfig}';
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is TransactionDetails &&
          runtimeType == other.runtimeType &&
          amount == other.amount &&
          currencyCode == other.currencyCode &&
          customerName == other.customerName &&
          customerEmail == other.customerEmail &&
          paymentReference == other.paymentReference &&
          paymentDescription == other.paymentDescription &&
          metaData == other.metaData &&
          paymentMethods == other.paymentMethods &&
          incomeSplitConfig == other.incomeSplitConfig;

  @override
  int get hashCode =>
      amount.hashCode ^
      currencyCode.hashCode ^
      customerName.hashCode ^
      customerEmail.hashCode ^
      paymentReference.hashCode ^
      paymentDescription.hashCode ^
      metaData.hashCode ^
      paymentMethods.hashCode ^
      incomeSplitConfig.hashCode;
}
