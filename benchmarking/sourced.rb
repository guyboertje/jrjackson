#!/usr/bin/env ruby

# require 'rubygems'
# require 'bigdecimal'
# require 'benchmark'
# require 'thread'
require 'digest'
require 'gson'

# require File.expand_path('lib/jrjackson')

HASH = {:one => nil, :two => nil, :three => nil, :four => {:a => nil, :b => nil, :c =>nil},
:five => {:d => nil, :e => nil},
:six => {:f => nil, :g => nil, :h =>[], :i => nil, :j => nil, :k => nil, :l => nil},
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

array_size = 100000

filename = File.expand_path('benchmarking/fixtures/source4.json')

array = Array.new(array_size)

File.open(filename, 'w') do |f|
  f.write( ::Gson::Encoder.new({}).encode(
      array.map{|e| randomize_entries(HASH.dup)}
    )
  )
end
