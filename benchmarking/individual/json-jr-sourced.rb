require 'bigdecimal'
require 'benchmark'
require 'json/ext'
require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

dumped_string = File.read(filename)

Benchmark.bmbm("jackson parse symbol + bigdecimal:  ".size) do |x|
  x.report("json java parse: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { ::JSON.parse(dumped_string) }
  end
end
