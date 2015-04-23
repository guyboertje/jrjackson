require 'bigdecimal'
require 'benchmark'
require 'virtus'

require File.expand_path('lib/jrjackson')
require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

opts = {use_bigdecimal: true, raw: true}

dumped_string = %Q|{
  "v_num": 3,
  "v_decimal": 3.333,
  "v_boolean" : true,
  "v_string": "message"
}|

class Model
  include Virtus.model

  attribute :v_string, String
  attribute :v_num, Integer
  attribute :v_boolean, Boolean
  attribute :v_decimal, Decimal

  def add_value(value, key)
    send("#{key}=", value)
  end
end

class ObjectModel
  attr_accessor :v_string, :v_num, :v_boolean, :v_decimal
  ATTRS = ['v_string','v_num','v_boolean','v_decimal']
  MAP = {'v_string'=>'v_string=' ,'v_num'=>'v_num=' ,'v_boolean'=>'v_boolean=' ,'v_decimal'=>'v_decimal='}
  MAP.default = 'nothing'

  def self.load(hash)
    new.load hash
  end

  def nothing
  end

  def load(hash)
    @v_string, @v_num, @v_boolean, @v_decimal = hash.values_at(*ATTRS)
  end

  def add_value(value, key)
    send(MAP[key], value)
  end

  def to_s
    "<ObjectModel @v_string: '#{@v_string}', @v_num: #{@v_num}, @v_boolean: #{@v_boolean}, @v_decimal: #{@v_decimal}"
  end
end

iter = 1000000

# model = ObjectModel.new
# JrJackson::Json.sj_load(model, dumped_string, opts)
# puts model.to_s


Benchmark.bmbm(BenchOptions.output_width) do |x| 
  x.report("jackson model load raw: #{iter}") do
    iter.times do
      ObjectModel.load JrJackson::Json.load(dumped_string, opts)
    end
  end
end

Benchmark.bmbm(BenchOptions.output_width) do |x| 
  x.report("jackson model load sj: #{iter}") do
    iter.times do
      model = ObjectModel.new
      JrJackson::Json.sj_load(model, dumped_string, opts)
    end
  end
end
