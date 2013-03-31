LICENSE applicable to this library:

Apache License 2.0 see http://www.apache.org/licenses/LICENSE-2.0

Jrjackson:

a jruby library wrapping the JAVA jackson jars

Version: 0.0.9

NOTE: Smile support has been temporarily dropped

The code has been refactored to use almost all Java.

There is shortly to be a MultiJson adapter added for JrJackson

provides:

```
JrJackson::Json.load(str, options) -> hash like object
  aliased as parse
JrJackson::Json.dump(obj) -> json string
  aliased as generate
```

By default the load method will return Ruby objects (Hashes have string keys).
The options hash respects two symbol keys
  :symbolize_keys
    Will return symbol keys in hashes
  :raw
    Will return JRuby wrapped java objects that quack like ruby objects
    This is the fastest option

Behind the scenes there are three Ruby sub modules of the JrJackson module
```
  JrJackson::Str
  JrJackson::Sym
  JrJackson::Raw

  These all have the same method signatures - they map to different java classes
  that parse appropriately
```

Credit to Chuck Remes for the benchmark and initial
investigation when the jruby, json gem and the jackson
libraries were young.

I compared Json (java) 1.7.7, Gson 0.6.1 and jackson 2.1.4 on jruby 1.7.3 and Java 7
```
                                         user     system      total        real
ruby parse:                         10.300000   0.020000  10.320000 ( 10.014000)
gson parse:                         11.270000   0.010000  11.280000 ( 10.958000)
jrjackson parse raw:                 4.840000   0.080000   4.920000 (  3.767000)
jrjackson parse symbol keys:         5.130000   0.010000   5.140000 (  4.975000)
jrjackson parse string keys:         7.370000   0.010000   7.380000 (  7.223000)
ruby generate:                      13.590000   0.050000  13.640000 ( 12.815000)
gson generate:                       5.080000   0.010000   5.090000 (  4.949000)
jackson generate:                    4.640000   0.010000   4.650000 (  4.560000)
```
