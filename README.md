# monnify_payment_sdk

Flutter plugin for Monnify SDK

[![pub package](https://img.shields.io/pub/v/monnify_payment_sdk.svg)](https://pub.dartlang.org/packages/monnify_payment_sdk)

## Getting Started
To use this plugin, add `monnify_payment_sdk` as a [dependency in your pubspec.yaml file](https://flutter.io/platform-plugins/).

## How to use
This plugin exposes two APIs:

### 1. Initialize

Initialize the plugin. This should be done once, preferably in the `initState` of your widget.

``` dart
import 'package:monnify_payment_sdk/monnify_payment_sdk.dart';

class _MyAppState extends State<MyApp> {

  ...

  late Monnify? monnify;

  @override
  void initState() {
    monnify = await Monnify.initialize(
      applicationMode: ApplicationMode.TEST,
      apiKey: 'YOUR API KEY',
      contractCode: 'YOUR CONTRACT CODE',
    );
    super.initState();
  }
  
  ...
}
```

### 2. Initialize Payment

Create an object of the Transaction class and pass it to the initializePayment function

``` dart
Future<void> initializePayment() async {
  final amount = '20.00';
    final paymentReference = 'YOUR PAYMENT REFERENCE';


    final transaction = TransactionDetails().copyWith(
      amount: amount,
      currencyCode: 'NGN',
      customerName: 'Customer Name',
      customerEmail: 'custo.mer@email.com',
      paymentReference: paymentReference,
      // metaData: {"ip": "0.0.0.0", "device": "mobile"},
      // paymentMethods: [PaymentMethod.CARD, PaymentMethod.ACCOUNT_TRANSFER, PaymentMethod.USSD],
      /*incomeSplitConfig: [SubAccountDetails("MFY_SUB_319452883968", 10.5, 500, true),
          SubAccountDetails("MFY_SUB_259811283666", 10.5, 1000, false)]*/
    );

    try {
      final response =
          await monnify?.initializePayment(transaction: transaction);
      } catch (e) { 
        // handle exceptions in here.
      }
}
```

Optional params:
 **Payment Methods** specify transaction-level payment methods.
 **Sub-Accounts** in incomeSplitConfig are accounts that will receive settlement for the particular transaction being initialized.
 **MetaData** is map with single depth for any extra information you want to pass along with the transaction.

The TransactionResponse class received after sdk is closed contains the below fields

```dart
String paymentDate;
double amountPayable;
double amountPaid;
String paymentMethod;
String transactionStatus;
String transactionReference;
String paymentReference;
String currencyCode;
```
** __Note: No TransactionResponse is returned on iOS dev if a user closes the Modal__


## Need more information?
For further info about Monnify's mobile SDKs, including transaction status types, 
see the documentations for the 
[Android](https://teamapt.atlassian.net/wiki/spaces/MON/pages/213909311/Monnify+Android+SDK) and 
[iOS](https://teamapt.atlassian.net/wiki/spaces/MON/pages/213909672/Monnify+iOS+SDK) SDKs