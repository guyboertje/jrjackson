require 'bigdecimal'
require 'benchmark'

require File.expand_path('lib/jrjackson')
require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

opts = {raw: true}
dumped_string = File.read(filename)

class ScHandler
  attr_accessor :result

  def initialize(arr = [])
    @result = arr
  end

  def hash_start()
    {}
  end

  def hash_end()
  end

  def hash_key(key)
    # return 'too' if 'two' == key
    # return :symbol if 'symbol' == key
    key
  end

  def array_start()
    []
  end

  def array_end()
  end

  def add_value(value)
    # @result = value
  end

  def hash_set(h, key, value)
    # h[key] = value
  end

  def array_append(a, value)
    # a.push(value)
  end
end

errors = []

Benchmark.bmbm(BenchOptions.output_width) do |x| 
  x.report("jackson parse sc: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times do
      handler = ScHandler.new(errors)
      JrJackson::Json.sc_load(handler, dumped_string, opts)
    end
  end
end
