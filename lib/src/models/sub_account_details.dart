/// Monnify allows you as a merchant create [SubAccountDetails] so you can be able to
/// split payments between different accounts. You can easily split a single
/// payment across multiple accounts. This means for one transaction, Monnify
/// can help you share the amount paid between up to 5 different accounts.
/// Read more [SubAccounts](https://teamapt.atlassian.net/wiki/spaces/MON/pages/213909218/Sub+Accounts)
class SubAccountDetails {
  String subAccountCode;
  double feePercentage;
  double splitAmount;
  bool feeBearer;

  SubAccountDetails({
    required this.subAccountCode,
    required this.feePercentage,
    required this.splitAmount,
    required this.feeBearer,
  });

  /// Converts from  [Map] to [SubAccountDetails]
  SubAccountDetails.fromMap(Map<String, dynamic> map)
      : subAccountCode = map['subAccountCode'],
        feePercentage = map['feePercentage'],
        splitAmount = map['splitAmount'],
        feeBearer = map['feeBearer'];

  /// Converts [SubAccountDetails] to [Map]
  Map<String, dynamic> toMap() => {
        'subAccountCode': subAccountCode,
        'feePercentage': feePercentage,
        'splitAmount': splitAmount,
        'feeBearer': feeBearer,
      };
}
