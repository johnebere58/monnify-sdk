import 'package:monnify_payment_sdk/src/models/application_mode.dart';
import 'package:monnify_payment_sdk/src/common/monnify_exception.dart';
import 'package:monnify_payment_sdk/src/models/transaction_details.dart';
import 'package:monnify_payment_sdk/src/platform_channel/monnify_payment_sdk_platform_interface.dart';
import 'package:monnify_payment_sdk/src/models/transaction_response.dart';

/// [Monnify](https://monnify.com) Payment SDK for flutter
///
/// [READMORE](https://teamapt.atlassian.net/wiki/spaces/MON/overview?homepageId=210468866)
class Monnify {
  final ApplicationMode applicationMode;
  final String apiKey;
  final String contractCode;

  const Monnify._(this.applicationMode, this.apiKey, this.contractCode);

  /// [initialize] method is used initialize the [Monnify] payment SDK.
  /// This method accepts [apiKey], [contractCode], [applicationMode]
  static Future<Monnify?> initialize({
    required ApplicationMode applicationMode,
    required String apiKey,
    required String contractCode,
  }) async {
    assert(() {
      if (apiKey.isEmpty) {
        throw MonnifyException('apiKey cannot be empty');
      }
      if (contractCode.isEmpty) {
        throw MonnifyException('contractCode cannot be empty');
      }
      return true;
    }());

    final monnify = Monnify._(
      applicationMode,
      apiKey,
      contractCode,
    );

    final initialized = await MonnifyPaymentSdkPlatform.instance.initialize(
        monnify.apiKey, monnify.contractCode, monnify.applicationMode);

    return initialized ? monnify : null;
  }

  /// [initializePayment] method is used to launch the the [Monnify] payment gateway
  /// with a specified [TransactionDetails].
  /// This method returns a [TransactionResponse], this only happens after the
  /// user completes or terminate the [Monnify] payment gateway
  Future<TransactionResponse?> initializePayment({
    required TransactionDetails transaction,
  }) async {
    return MonnifyPaymentSdkPlatform.instance.initializePayment(transaction);
  }
}
