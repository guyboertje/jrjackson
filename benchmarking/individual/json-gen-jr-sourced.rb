require 'bigdecimal'
require 'benchmark'
require 'json/ext'

require File.expand_path('lib/jrjackson')
require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)
hsh = JSON.parse(File.read(filename))

Benchmark.bmbm(BenchOptions.output_width) do |x|
  x.report("json java generate: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { JSON.generate(hsh) }
  end
end
