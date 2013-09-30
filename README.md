LICENSE applicable to this library:

Apache License 2.0 see http://www.apache.org/licenses/LICENSE-2.0

Jrjackson:

a jruby library wrapping the JAVA Jackson jars

Version: 0.2.0

NOTE: Smile support has been temporarily dropped

The code has been refactored to use almost all Java.

There is now a MultiJson adapter added for JrJackson

JrJackson provides:

```
JrJackson::Json.load(string, options) -> hash like object
  aliased as parse
JrJackson::Json.dump(obj) -> json string
  aliased as generate
```

By default the load method will return Ruby objects (Hashes have string keys).
The options hash respects three symbol keys
  :symbolize_keys
    Will return symbol keys in hashes
  :raw
    Will return JRuby wrapped java objects that quack like ruby objects
    This is the fastest option
  :use_bigdecimal
    Will return BigDecimal objects instead of Float
    If used with the :raw option you will get Java::JavaMath::BigDecimal objects
    otherwise they are Ruby BigDecimal

Note: the dump method expects that the values of hashes or arrays are JSON data types,
the only exception to this is Ruby Symbol as values, they are converted to java strings
during serialization. All other objects should be converted to JSON data types before
serialization.

There are two Ruby sub modules of the JrJackson module

JrJackson::Json, this is the general external facade used by MultiJson, and is pure Ruby
JrJackson::Raw, this is used by the Json module, it is defined in Java with annotations
exposing it as a Ruby module with module methods.

If you are using JrJackson directly and not via MultiJson then you can take advantage of
optimised Java methods parse_raw, parse_sym and parse_str. Please look at the tests and
benchmarks for examples. 

Credit to Chuck Remes for the benchmark and initial
investigation when the jruby, json gem and the jackson
libraries were young.

I compared Json (java) 1.8, Gson 0.6.1 and jackson 2.2.2 on jruby 1.7.4 and OpenJDK 64-Bit Server VM 1.7.0_21-b02
```
                                         user     system      total        real
json java parse:                    10.140000   0.020000  10.160000 (  9.867000)
gson parse:                         11.510000   0.010000  11.520000 ( 11.164000)
jackson parse raw:                   4.320000   0.020000   4.340000 (  3.552000)
jackson parse symbol keys:           5.910000   0.010000   5.920000 (  5.770000)
jackson parse string keys:           8.400000   0.010000   8.410000 (  8.232000)
json java generate:                 14.730000   0.010000  14.740000 ( 13.864000)
gson generate:                       5.060000   0.010000   5.070000 (  4.981000)
jackson generate:                    4.540000   0.000000   4.540000 (  4.467000)
```
