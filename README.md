LICENSE applicable to this library:

Apache License 2.0 see http://www.apache.org/licenses/LICENSE-2.0

### JrJackson:

a jruby library wrapping the JAVA Jackson jars`

Version: 0.2.1

__NOTE:__ Smile support has been temporarily dropped

The code has been refactored to use almost all Java.

There is now a MultiJson adapter added for JrJackson

JrJackson provides:

```
JrJackson::Json.load(string, options) -> hash like object
      aliased as parse
```
By default the load method will return Ruby objects (Hashes have string keys).
The options hash respects three symbol keys

+ :symbolize_keys

  Will return symbol keys in hashes

+ :raw

  Will return JRuby wrapped java objects that quack like ruby objects
  This is the fastest option

+ :use_bigdecimal

  Will return BigDecimal objects instead of Float
  If used with the :raw option you will get Java::JavaMath::BigDecimal objects
  otherwise they are Ruby BigDecimal

```
JrJackson::Json.dump(obj) -> json string
      aliased as generate
```
The dump method expects that the values of hashes or arrays are JSON data types,
the only exception to this is Ruby Symbol as values, they are converted to java strings
during serialization. __NOTE:__ All other objects should be converted to JSON data types before
serialization. See the wiki for more on this.

***

There are two Ruby sub modules of the JrJackson module

```JrJackson::Json```, this is the general external facade used by MultiJson, and is pure Ruby.

```JrJackson::Raw```, this is used by the Json module, it is defined in Java with annotations
exposing it as a Ruby module with module methods.

Credit to Chuck Remes for the benchmark and initial
investigation when the jruby, json gem and the jackson
libraries were young.

***

I compared Json (java) 1.8, Gson 0.6.1 and jackson 2.2.3 on jruby 1.7.4 and OpenJDK 64-Bit Server VM 1.7.0_21-b02
All the benchmarks were run separately. A 727.9KB string of random json data is read from a file and handled 250 times, thereby attempting to balance invocation and parsing benchmarking.

```
generation/serialize

                                         user     system      total         real
json mri generate: 250                  12.02       0.00      12.02     ( 12.022)
oj mri generate: 250                     7.18       0.00       7.18     (  7.183)
json java generate: 250                  7.83       0.01       7.84     (  7.289)
gson generate: 250                       5.44       0.00       5.44     (  5.387)
jackson generate: 250                    5.32       0.00       5.32     (  5.146)

parsing/deserialize - after jrjackson parsing profiling

                                         user     system      total         real
json mri parse: 250                      8.35       0.02       8.37     (  8.366)
oj mri parse: 250                        6.10       0.13       6.23     (  7.527)

gson parse: 250                         12.02       0.02      12.04     ( 11.774)
json java parse: 250                    10.35       0.01      10.36     ( 10.204)
jackson parse string keys: 250           6.27       0.02       6.29     (  6.010)
jackson parse string + bigdecimal: 250   6.27       0.00       6.27     (  5.973)
jackson parse symbol keys: 250           5.16       0.00       5.16     (  4.873)
jackson parse symbol + bigdecimal: 250   4.75       0.06       4.81     (  4.461)
jackson parse raw: 250                   3.23       0.05       3.28     (  3.021)
jackson parse raw + bigdecimal: 250      3.06       0.06       3.12     (  2.681)

```



