#!/usr/bin/env ruby

require 'bigdecimal'
require 'benchmark/ips'
require 'json/ext'
require 'time'

$LOAD_PATH.unshift File.expand_path('../../../lib', __FILE__)
require 'jrjackson'

obj = {
  'a' => 'Alpha', # string
  'b' => true,    # boolean
  'c' => 12345,   # number
  'd' => [ true, [false, [-123456789, nil], 3.9676, ['Something else.', false], nil]], # mix it up array
  'e' => { 'zero' => nil, 'one' => 1, 'two' => 2, 'three' => [3], 'four' => [0, 1, 2, 3, 4] }, # hash
  'f' => nil,     # nil
  'g' => Date.today,
  'h' => { 'a' => { 'b' => { 'c' => { 'd' => {'e' => { 'f' => { 'g' => nil }}}}}}}, # deep hash, not that deep
  'i' => [[[[[[[nil]]]]]]],  # deep array, again, not that deep
  'j' => Java::JavaUtil::ArrayList.new(["foo", 1])
}

Benchmark.ips do |x|
  x.config(:time => 120, :warmup => 180)

  x.report("jrjackson") { JrJackson::Base.generate(obj) }
  x.report("JSON")      { JSON.dump(obj) }

  x.compare!
end
