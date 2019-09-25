#!/usr/bin/env ruby

require 'rubygems'
require 'bigdecimal'
require 'benchmark'
require 'thread'
require 'digest'
require 'json/ext'
require 'gson'

require File.expand_path('lib/jrjackson_jars')
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

def random_date
  Time.at(rand * Time.now.to_i)
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

def randomize_entries hsh, include_dates
  new_hsh = {}
  hsh.each_pair do |key, value|
    case value
    when NilClass
      new_hsh[key] = send METHODS[rand(include_dates ? 4 : 3)]
    when Hash
      new_hsh[key] = randomize_entries value, include_dates
    when Array
      new_hsh[key] = fill_array
    end
  end
  new_hsh
end

METHODS = [:random_string, :random_number, :random_float, :random_date]

org_array = []
no_dates_array = []
one = []

0.upto(5000) do |i|
  hsh = HASH.dup
  org_array << randomize_entries(hsh, true)
end

0.upto(5000) do |i|
  hsh = HASH.dup
  no_dates_array << randomize_entries(hsh, false)
end


generated_array = []
generated_smile = []

org_array.each do |hsh|
  generated_array << JrJackson::Base.generate(hsh)
end

q = Queue.new


Benchmark.bmbm("Jackson generate with no dates".size) do |x|
  x.report("Jackson generate with dates") do
    threads = 100.times.map do
      Thread.new do
        10.times.each do
          org_array.each do |hsh|
            JrJackson::Base.generate(hsh)
          end
        end
      end
    end
    threads.each(&:join)
  end

  x.report("Jackson generate with no dates") do

    threads = 100.times.map do
      Thread.new do
        10.times.each do
          no_dates_array.each do |hsh|
            JrJackson::Base.generate(hsh)
          end
        end
      end
    end
    threads.each(&:join)
  end
end

Benchmark.bmbm("jackson parse symbol keys:  ".size) do |x|

  x.report("json java parse:") do
    th1 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| ::JSON.parse(string).first }}
      q << true
    end
    th2 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| ::JSON.parse(string).first }}
      q << true
    end
    q.pop
    q.pop
  end

  x.report("gson parse:") do
    th1 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| ::Gson::Decoder.new.decode(string) }}
      q << true
    end
    th2 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| ::Gson::Decoder.new.decode(string) }}
      q << true
    end
    q.pop
    q.pop
  end

  x.report("jackson parse raw:") do
    th1 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Raw.parse_raw(string) }}
      q << true
    end
    th2 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Raw.parse_raw(string) }}
      q << true
    end
    q.pop
    q.pop
  end

  x.report("jackson parse symbol keys:") do
    th1 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Raw.parse_sym(string) }}
      q << true
    end
    th2 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Raw.parse_sym(string) }}
      q << true
    end
    q.pop
    q.pop
  end

  x.report("jackson parse string keys:") do
    th1 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Raw.parse_str(string) }}
      q << true
    end
    th2 = Thread.new(generated_array) do |arry|
      50.times {arry.each {|string| JrJackson::Raw.parse_str(string) }}
      q << true
    end
    q.pop
    q.pop
  end
end
