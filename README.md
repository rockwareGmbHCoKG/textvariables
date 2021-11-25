# ![Rockware](https://rockware.info/Default-small.png) 
# Textvariables for AEM

[![Build](https://github.com/rockwareGmbHCoKG/textvariables/workflows/Build/badge.svg?branch=develop)](https://github.com/rockwareGmbHCoKG/textvariables/actions?query=workflow%3ABuild+branch%3Adevelop)
[![codecov](https://codecov.io/gh/rockwareGmbHCoKG/textvariables/branch/main/graph/badge.svg)](https://codecov.io/gh/lovepoem/codecov-travis-maven-junit5-example)

If your editors want to reuse smaller content parts like phone numbers, fees or even content blocks with rich text inside this package might be exactly
what you need.

## How to use it

### Prerequisites
- Download and install the latest release package (tested with AEM 6.5.9). 
- Make sure [io.wcm.caconfig.editor.package-1.7.0.zip](https://mvnrepository.com/artifact/io.wcm/io.wcm.caconfig.editor.package) or a newer version is installed on your AEM server.
- You will also need [io.wcm.caconfig.extensions-1.7.0.jar](https://mvnrepository.com/artifact/io.wcm/io.wcm.caconfig.extensions) or newer.
- Last but not least: Make sure that you allow the template /apps/wcm-io/caconfig/editor-package/templates/editor in your content path to be used - or of course you can also use your own config template instead.

### Step by Step
- The project contains two components "textvariables/components/token" and "textvariables/components/richToken". You may use those components (preferably in a container) to edit token keys and values.
- But of course you can create your own components as well.
- Both have two properties: _tokenKey_ (holds the key or variable) and _tokenValue_ (holds the value that will be inserted at render time).
- Add a page to your content tree that will be used to store all the key / value pairs in. 
- If all went well, a new *Context Aware Configuration* with name **Text Variable Token Config** will be available for your CaConfig page.
- Open your CaConfig (or create a new one).
- Create an instance of this new configuration.
- If you are using the components from this bundle to enter token keys and values, leave token key and token value as they are.
- Replace Tokens -> uncheck to have your tokens not replaced any more.
- Resource Type for Component: no change if our components are used, otherwise enter values matching your own components.
- Token Page Path: Select the page that holds your token keys and values.
- Edit token keys and values.
- Use token keys in your content. If your token key is _token1_, you need to write _${token1}_ in the content. Token keys are only replaced in preview mode or with WCM mode disabled.
- Make sure you publish the page with the token keys after you did some changes there.

## Dependencies
- [io.wcm.caconfig.editor.package-1.7.0.zip](https://mvnrepository.com/artifact/io.wcm/io.wcm.caconfig.editor.package)
- [io.wcm.caconfig.extensions-1.7.0.jar](https://mvnrepository.com/artifact/io.wcm/io.wcm.caconfig.extensions)

## License
Apache 2
