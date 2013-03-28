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
JrJackson::Json.parse(str, options) -> hash like object
  aliased as load
JrJackson::Json.generate(obj) -> json string
  aliased as dump
```

By default the parse method will return a full Ruby object with stringed keys.
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

I compared json-jruby 1.6.1 and jackson/smile 2.1.4 on jruby 1.7.3 and Java 7
```
                                         user     system      total        real
ruby parse:                         10.320000   0.010000  10.330000 ( 10.038000)
jackson parse raw:                   4.590000   0.040000   4.630000 (  3.762000)
jackson parse symbol keys:           5.140000   0.010000   5.150000 (  4.969000)
jackson parse string keys:           7.520000   0.000000   7.520000 (  7.351000)
ruby generate:                      13.700000   0.030000  13.730000 ( 12.893000)
jackson generate:                    4.550000   0.010000   4.560000 (  4.462000)

```
