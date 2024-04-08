class MonnifyException implements Exception {
  final String message;

  MonnifyException(this.message);

  @override
  String toString() {
    return 'MonnifyInitializationException{message: $message}';
  }
}
