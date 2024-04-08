import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:monnify_payment_sdk/monnify_payment_sdk.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: MonnifyPaymentSDKDemo(),
    );
  }
}

class MonnifyPaymentSDKDemo extends StatefulWidget {
  const MonnifyPaymentSDKDemo({super.key});

  @override
  State<MonnifyPaymentSDKDemo> createState() => _MonnifyPaymentSDKDemoState();
}

class _MonnifyPaymentSDKDemoState extends State<MonnifyPaymentSDKDemo> {
  late final TextEditingController controller = TextEditingController()
    ..text = '20';

  late Monnify? monnify;

  @override
  void initState() {
    initializeMonnify();
    super.initState();
  }

  void showToast(String message) {
    final scaffold = ScaffoldMessenger.of(context);
    scaffold.showSnackBar(
      SnackBar(
        content: Text(message),
        action: SnackBarAction(
            label: 'CLOSE', onPressed: scaffold.hideCurrentSnackBar),
      ),
    );
  }

  void initializeMonnify() async {
    monnify = await Monnify.initialize(
      applicationMode: ApplicationMode.TEST,
      apiKey: 'YOUR API KEY',
      contractCode: 'YOUR CONTRACT CODE',
    );
  }

  void onInitializePayment() async {
    final amount = double.parse(controller.text);
    final paymentReference = DateTime.now().millisecondsSinceEpoch.toString();

    final transaction = TransactionDetails().copyWith(
      amount: amount,
      currencyCode: 'NGN',
      customerName: 'Customer Name',
      customerEmail: 'custo.mer@email.com',
      paymentReference: paymentReference,
      // metaData: {"ip": "196.168.45.22", "device": "mobile"},
      // paymentMethods: [PaymentMethod.CARD, PaymentMethod.ACCOUNT_TRANSFER, PaymentMethod.USSD],
      /*incomeSplitConfig: [SubAccountDetails("MFY_SUB_319452883968", 10.5, 500, true),
          SubAccountDetails("MFY_SUB_259811283666", 10.5, 1000, false)]*/
    );

    try {
      final response =
          await monnify?.initializePayment(transaction: transaction);

      showToast(response.toString());
      log(response.toString());
    } catch (e) {
      log('$e');
      showToast(e.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 30),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextFormField(
              controller: controller,
              keyboardType: TextInputType.number,
              inputFormatters: [
                FilteringTextInputFormatter.digitsOnly,
              ],
              decoration: const InputDecoration(hintText: 'Amount'),
            ),
            const SizedBox(height: 20),
            TextButton(onPressed: onInitializePayment, child: const Text('Pay'))
          ],
        ),
      ),
    );
  }
}
