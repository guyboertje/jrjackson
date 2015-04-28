require 'bigdecimal'
require 'benchmark'

require File.expand_path('lib/jrjackson')
require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

opts = nil
dumped_string = File.read(filename)

Benchmark.bmbm(BenchOptions.output_width) do |x|
  x.report("jackson parse symbol ruby: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { JrJackson::Json.load_ro(dumped_string, opts) }
  end
end
