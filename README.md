LICENSE applicable to this library:

Apache License 2.0 see http://www.apache.org/licenses/LICENSE-2.0



Jrjackson:

a jruby library wrapping the JAVA jackson jars

provides:

JrJackson::Json.generate(obj) -> json string
JrJackson::Json.parse(str) -> hash like object

JrJackson::Smile.generate obj -> smile string
JrJackson::Smile.parse smile_str -> hash like object


Credit to Chuck Remes for the benchmark and initial
investigation when the jruby, json gem and the jackson
libraries were young.

I compared json-jruby 1.6.1 and jackson/smile 2.1.3 on jruby 1.7.2 and Java 7
```
                        user     system      total        real
ruby generate:     12.310000   0.030000  12.340000 ( 11.946000)
jackson generate:   4.260000   0.010000   4.270000 (  4.037000)
ruby parse:         8.450000   0.000000   8.450000 (  8.297000)
jackson parse:      3.770000   0.000000   3.770000 (  3.241000)
```

Variants:

jrjackson.rb
  this variant is the fastest but the parse function will
  return Java ArrayList instead of Ruby Array and
  Java LinkedHashMap instead of Ruby Hash.
  In Jruby 1.6.0+, these java classes have most if not all
  of the behaviour of the corresponding Ruby classes

jrjackson_r.rb
  If you absolutely need Ruby Objects then use this variant
  It parses a little bit slower than the above but
  returns Ruby Array and Hash objects

jrjackson_r_sym.rb
  Will give you symbols for hash keys instead of strings

