require 'rubygems'
require 'benchmark'
require 'digest'
require 'json'
require 'lib/jrjackson'

HASH = {:one => nil, :two => nil, :three => nil, :four => {:a => nil, :b => nil, :c =>nil},
:five => {:d => nil, :e => nil},
:six => {:f => nil, :g => nil, :h =>nil, :i => nil, :j => nil, :k => nil, :l => nil},
:seven => nil, :eight => nil,
:nine => {:m => {:A => nil, :B => nil}}}

def random_string
  Digest::MD5.hexdigest "#{Time.now + rand(999)}"
end

def random_number
  rand 999_999_999
end

def random_float
  random_number + rand
end

def randomize_entries hsh
  new_hsh = {}
  hsh.each_pair do |key, value|
    case value
    when NilClass
      new_hsh[key] = send METHODS[rand(3)]
    when Hash
      new_hsh[key] = randomize_entries value
    end
  end
  new_hsh
end

METHODS = [:random_string, :random_number, :random_float]

org_array = []
one = []
#
0.upto(50000) do |i|
  hsh = HASH.dup
  org_array << randomize_entries(hsh)
end

generated_array = []
generated_smile = []


org_array.each do |hsh|
  generated_array << JrJackson::Json.generate(hsh)
  generated_smile << JrJackson::Smile.generate(hsh)
end

Benchmark.bmbm("jackson generate: plus some margin".size) do |x|

  x.report("jackson generate:") do
    org_array.each {|hsh| JrJackson::Json.generate(hsh)  }
  end

  x.report("smile generate:") do
    org_array.each {|hsh| JrJackson::Smile.generate(hsh)  }
  end

  x.report("ruby generate:") do
    org_array.each {|hsh|  JSON.fast_generate(hsh) }
  end

  x.report("jackson parse:") do
    generated_array.each {|string| JrJackson::Json.parse(string) }
  end

  x.report("smile parse:") do
    generated_smile.each {|string| JrJackson::Smile.parse(string)  }
  end

  x.report("ruby parse:") do
    generated_array.each {|string| JSON.parse(string) }
  end
end
