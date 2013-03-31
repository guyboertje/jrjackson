#!/usr/bin/env ruby

require 'rubygems'
require 'benchmark'
require 'thread'
require 'digest'
require 'json'
require 'gson'

require File.expand_path('lib/jrjackson')

HASH = {:one => nil, :two => nil, :three => nil, :four => {:a => nil, :b => nil, :c =>nil},
:five => {:d => nil, :e => nil},
:six => {:f => nil, :g => nil, :h =>nil, :i => nil, :j => nil, :k => nil, :l => nil},
:seven => nil, :eight => [],
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

def fill_array
  value = []
  5.times do
    value.push send(METHODS[rand(3)])
  end
  value
end

def randomize_entries hsh
  new_hsh = {}
  hsh.each_pair do |key, value|
    case value
    when NilClass
      new_hsh[key] = send METHODS[rand(3)]
    when Hash
      new_hsh[key] = randomize_entries value
    when Array
      new_hsh[key] = fill_array
    end
  end
  new_hsh
end

METHODS = [:random_string, :random_number, :random_float]

org_array = []
one = []

0.upto(5000) do |i|
  hsh = HASH.dup
  org_array << randomize_entries(hsh)
end

generated_array = []
generated_smile = []
q = Queue.new

org_array.each do |hsh|
  generated_array << JrJackson::Raw.generate(hsh)
end

Benchmark.bmbm("jackson parse symbol keys:  ".size) do |x|

  x.report("json java parse:") do
    th1 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| ::JSON.parse("[#{string}]").first }}
      q << true
    end
    th2 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| ::JSON.parse("[#{string}]").first }}
      q << true
    end
    q.pop
    q.pop
    # 50.times {generated_array.each {|string| ::JSON.parse("[#{string}]").first }}
  end

  x.report("gson parse:") do
    th1 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| ::Gson::Decoder.new({}).decode(string) }}
      q << true
    end
    th2 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| ::Gson::Decoder.new({}).decode(string) }}
      q << true
    end
    q.pop
    q.pop
    # 50.times {generated_array.each {|string| ::Gson::Decoder.new({}).decode(string) }}
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
    # 50.times {generated_array.each {|string| JrJackson::Raw.parse(string) }}
  end

  x.report("jackson parse symbol keys:") do
    th1 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Sym.parse(string) }}
      q << true
    end
    th2 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Sym.parse(string) }}
      q << true
    end
    q.pop
    q.pop
    # 50.times {generated_array.each {|string| JrJackson::Sym.parse(string) }}
  end

  x.report("jackson parse string keys:") do
    th1 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Str.parse(string) }}
      q << true
    end
    th2 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Str.parse(string) }}
      q << true
    end
    q.pop
    q.pop
    # 50.times {generated_array.each {|string| JrJackson::Str.parse(string) }}
  end

  x.report("json java generate:") do
    50.times {org_array.each {|hsh|  hsh.to_json }}
  end

  x.report("gson generate:") do
    50.times {org_array.each {|hsh|  ::Gson::Encoder.new({}).encode(hsh) }}
  end

  x.report("jackson generate:") do
    50.times {org_array.each {|hsh| JrJackson::Raw.generate(hsh)  }}
  end
end
