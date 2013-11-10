require 'benchmark'
require 'json'
require 'json/ext'

require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

JSON.parser = JSON::Ext::Parser
hsh = JSON.parse(File.read(filename))

Benchmark.bmbm(BenchOptions.output_width) do |x|
  x.report("json mri generate: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times { JSON.fast_generate(hsh) }
  end
end
