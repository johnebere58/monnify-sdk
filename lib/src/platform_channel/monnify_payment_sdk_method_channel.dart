import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:monnify_payment_sdk/src/models/application_mode.dart';
import 'package:monnify_payment_sdk/src/models/transaction_details.dart';
import 'package:monnify_payment_sdk/src/platform_channel/monnify_payment_sdk_platform_interface.dart';
import 'package:monnify_payment_sdk/src/models/transaction_response.dart';

/// An implementation of [MonnifyPaymentSdkPlatform] that uses method channels.
class MethodChannelMonnifyPaymentSdk extends MonnifyPaymentSdkPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('monnify_payment_sdk');

  /// [initialize] method is used initialize the [Monnify] payment SDK.
  /// This method accepts [apiKey], [contractCode], [applicationMode]
  @override
  Future<bool> initialize(String apiKey, String contractCode,
      ApplicationMode applicationMode) async {
    Map<String, String> args = {
      "apiKey": apiKey,
      "contractCode": contractCode,
      "applicationMode": describeEnum(applicationMode)
    };

    final initialized =
        await methodChannel.invokeMethod<bool>('initialize', args);
    return initialized ?? false;
  }

  /// [initializePayment] method is used to launch the the [Monnify] payment gateway
  /// with a specified [TransactionDetails].
  /// This method returns a [TransactionResponse], this only happens after the
  /// customer completes or terminate the [Monnify] payment gateway
  @override
  Future<TransactionResponse> initializePayment(
      TransactionDetails transaction) async {
    final result = await methodChannel.invokeMethod(
        "initializePayment", transaction.toMap());

    return TransactionResponse.fromMap(Map<String, dynamic>.from(result));
  }
}
