# ![Rockware](https://rockware.info/Default-small.png) 
# Textvariables for AEM

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
- Create an instance of this new configuration.
- 

## Dependencies

## License