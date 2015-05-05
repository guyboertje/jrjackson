require 'bigdecimal'
require 'oj'
require 'json/ext'
require 'benchmark/ips'


obj = {
      'a' => 'Alpha', # string
      'b' => true,    # boolean
      'c' => 12345,   # number
      'd' => [ true, [false, [-123456789, nil], 3.9676, ['Something else.', false], nil]], # mix it up array
      'e' => { 'zero' => nil, 'one' => 1, 'two' => 2, 'three' => [3], 'four' => [0, 1, 2, 3, 4] }, # hash
      'f' => nil,     # nil
      'h' => { 'a' => { 'b' => { 'c' => { 'd' => {'e' => { 'f' => { 'g' => nil }}}}}}}, # deep hash, not that deep
      'i' => [[[[[[[nil]]]]]]]  # deep array, again, not that deep
    }
json_source = Oj.dump(obj)

puts "Json string size: #{json_source.size}"
Benchmark.ips do |x|
  x.config(:time => 5, :warmup => 5)

  x.report("oj gem") { Oj.strict_load(json_source) }
  x.report("json gem") { JSON::Ext::Parser.new(json_source).parse }
  
end
