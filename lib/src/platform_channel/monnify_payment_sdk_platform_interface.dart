import 'package:monnify_payment_sdk/src/models/application_mode.dart';
import 'package:monnify_payment_sdk/src/platform_channel/monnify_payment_sdk_method_channel.dart';
import 'package:monnify_payment_sdk/src/models/transaction_details.dart';
import 'package:monnify_payment_sdk/src/models/transaction_response.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

abstract class MonnifyPaymentSdkPlatform extends PlatformInterface {
  /// Constructs a MonnifyPaymentSdkPlatform.
  MonnifyPaymentSdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static MonnifyPaymentSdkPlatform _instance = MethodChannelMonnifyPaymentSdk();

  /// The default instance of [MonnifyPaymentSdkPlatform] to use.
  ///
  /// Defaults to [MethodChannelMonnifyPaymentSdk].
  static MonnifyPaymentSdkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MonnifyPaymentSdkPlatform] when
  /// they register themselves.
  static set instance(MonnifyPaymentSdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool> initialize(
      String apiKey, String contractCode, ApplicationMode applicationMode) {
    throw UnimplementedError('initialize() has not been implemented.');
  }

  Future<TransactionResponse> initializePayment(
      TransactionDetails transaction) {
    throw UnimplementedError('initializePayment() has not been implemented.');
  }
}
