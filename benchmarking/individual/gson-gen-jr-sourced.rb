require 'bigdecimal'
require 'benchmark'
require 'gson'

require File.expand_path('lib/jrjackson')
require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)
hsh = JrJackson::Json.load(File.read(filename), use_bigdecimal: false, symbolize_keys: true)

Benchmark.bmbm(BenchOptions.output_width) do |x|
  x.report("gson generate: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { ::Gson::Encoder.new({}).encode(hsh) }
  end
end
