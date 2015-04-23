require 'bigdecimal'
require 'benchmark'
require 'oj'
require File.expand_path('benchmarking/fixtures/bench_options')

filename = File.expand_path(BenchOptions.source)

opts = {}
dumped_string = File.read(filename)

class SjHandler
  attr_reader :errors
  def initialize(arr = [])
    @errors = arr
  end

  def hash_start(key)

  end

  def hash_end(key)

  end

  def array_start(key)

  end

  def array_end(key)

  end

  def add_value(value, key)

  end

  def error(message, line, column)
    @errors << [:error, message, line, column]
  end
end

errors = []

Benchmark.bmbm(BenchOptions.output_width) do |x| 
  x.report("jackson parse sj: #{BenchOptions.iterations}") do
    BenchOptions.iterations.times do
      handler = SjHandler.new(errors)
      Oj.saj_parse(handler, dumped_string, opts)
    end
  end
end
