require 'bigdecimal'
require 'benchmark'

require File.expand_path('lib/jrjackson')
require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

opts = {raw: true}
dumped_string = File.read(filename)

Benchmark.bmbm(BenchOptions.output_width) do |x| 
  x.report("jackson parse ruby: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { JrJackson::Ruby.parse(dumped_string, opts) }
  end
end

Benchmark.bmbm(BenchOptions.output_width) do |x| 
  x.report("jackson parse java: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { JrJackson::Java.parse(dumped_string, opts) }
  end
end
