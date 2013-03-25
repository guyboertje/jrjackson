#!/usr/bin/env ruby

require 'rubygems'
require 'benchmark'
require 'thread'
require 'digest'
require 'json'
# require File.expand_path('lib/jrjackson_r')
require File.expand_path('lib/jrjackson')

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

0.upto(2500) do |i|
  hsh = HASH.dup
  org_array << randomize_entries(hsh)
end

generated_array = []
generated_smile = []
q = Queue.new

org_array.each do |hsh|
  generated_array << JrJackson::Raw.generate(hsh)
end

Benchmark.bmbm("jackson generate: plus some margin".size) do |x|

  x.report("ruby parse:") do
    100.times {generated_array.each {|string| JSON.parse(string) }}
  end

  x.report("jackson parse raw:") do
    th1 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Raw.parse(string) }}
      q << true
    end
    th2 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Raw.parse(string) }}
      q << true
    end
    q.pop
    q.pop
    # 100.times {generated_array.each {|string| JrJackson::Raw.parse(string) }}
  end

  x.report("jackson parse symbol keys:") do
    100.times {generated_array.each {|string| JrJackson::Sym.parse(string) }}
  end

  x.report("jackson parse string keys:") do
    100.times {generated_array.each {|string| JrJackson::Str.parse(string) }}
  end

  x.report("ruby generate:") do
    100.times {org_array.each {|hsh|  JSON.fast_generate(hsh) }}
  end

  x.report("jackson generate:") do
    100.times {org_array.each {|hsh| JrJackson::Raw.generate(hsh)  }}
  end
end
