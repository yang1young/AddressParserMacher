# CreditCardApplicationAddressMatch
To solve the inaccurate address match problem in financial fraud detection
as for a address ,do address cleaning and normalize,then parse it, using Baidu Map
API to improve the accuracy
* Address clean and normalize
* Address parse
* Address similarity calculate and matching
* Baidu API searching 

## install guide
1. apply for Baidu Map API token
2. Install Mysql and mysql drivers
3. install and configure java&maven environment
4. happily run AddressMatch.java

## file introduce
1. AddressCleanMatchUtils.java  some tools of address cleaning and matching
2. AddressMatch.java  main function of whole project
3. AddressStringParser.java  parse address according to keyword and rules
4. BaiduMapAPIQueryTools.java  using Baidu map API to get accuracy result
5. GetProvinceFromDatabase.java  mysql option, you can store data into database and using
   National Administrative divisions to get better result


