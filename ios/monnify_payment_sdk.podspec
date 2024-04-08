#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint monnify_payment_sdk.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'monnify_payment_sdk'
  s.version          = '1.0.0'
  s.summary          = 'Flutter plugin for monnify sdk.'
  s.description      = <<-DESC
Allows for making payments through the monnify system.
                       DESC
  s.homepage         = 'http://monnify.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Monnify LTD' => 'developers@monnify.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'MonnifyiOSSDK', '~> 0.2.6'
  s.platform = :ios, '13.0'
  s.ios.deployment_target = '10.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
