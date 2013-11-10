require 'bigdecimal'
require 'benchmark'
require 'json/ext'

require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

dumped_string = File.read(filename)

Benchmark.bmbm(BenchOptions.output_width) do |x|
  x.report("json mri parse: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { JSON::Ext::Parser.new(dumped_string).parse }
  end
end
