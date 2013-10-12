require 'bigdecimal'
require 'benchmark'

require File.expand_path('lib/jrjackson')
require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

opts = {use_bigdecimal: true, raw: true}
dumped_string = File.read(filename)

Benchmark.bmbm("jackson parse symbol + bigdecimal:  ".size) do |x| 
  x.report("jackson parse raw + bigdecimal: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { JrJackson::Json.load(dumped_string, opts) }
  end
end
