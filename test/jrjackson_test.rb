$LOAD_PATH << File.expand_path('../../lib', __FILE__)

require 'test/unit'
require 'thread'
require 'bigdecimal'
require 'jrjackson'

class JrJacksonTest < Test::Unit::TestCase

  def test_threading
    q1, q2, q3 = Queue.new, Queue.new, Queue.new

    s1 = %Q|{"a":2.5, "b":0.214, "c":3.4567, "d":-3.4567}|
    th1 = Thread.new(s1) do |string|
      q1 << JrJackson::Json.load(string, {use_bigdecimal: true, raw: true})
    end
    th2 = Thread.new(s1) do |string|
      q2 << JrJackson::Json.load(string, {use_bigdecimal: true, symbolize_keys: true})
    end
    th3 = Thread.new(s1) do |string|
      q3 << JrJackson::Json.load(string, {use_bigdecimal: false, symbolize_keys: true})
    end
    a1, a2, a3 = q1.pop, q2.pop, q3.pop
    assert_equal ["a", "b", "c", "d"], a1.keys
    assert a1.values.all? {|v| v.is_a?(Java::JavaMath::BigDecimal)}, "Expected all values to be Java::JavaMath::BigDecimal"
    assert_equal [:a, :b, :c, :d], a2.keys
    assert a2.values.all? {|v| v.is_a?(BigDecimal)}, "Expected all values to be BigDecimal"
    assert a3.values.all? {|v| v.is_a?(Float)}, "Expected all values to be Float"
  end

  def test_serialize_symbols_as_values
    source = {"first" => :first_symbol, "second" => {"inner" => :inner_symbol}}
    expected = {:first => "first_symbol", :second => {:inner => "inner_symbol"}}
    actual = JrJackson::Json.load(JrJackson::Json.dump(source), :symbolize_keys => true)
    assert_equal expected, actual
  end

  def test_can_parse_big_decimals
    expected = BigDecimal.new '0.12345678901234567890123456789'
    json = '{"foo":0.12345678901234567890123456789}'

    actual = JrJackson::Json.parse(json, :use_bigdecimal => true)['foo']
    assert_bigdecimal_equal expected, actual

    actual = JrJackson::Json.parse(json, :use_bigdecimal => true, :symbolize_keys => true)[:foo]
    assert_bigdecimal_equal expected, actual

    actual = JrJackson::Json.parse(json, :use_bigdecimal => true, :raw => true)['foo']
    assert_bigdecimal_similar expected, actual
  end

  def assert_bigdecimal_equal(expected, actual)
    assert_equal expected, actual
    assert_equal expected.class, actual.class
    assert_equal BigDecimal, actual.class
  end

  def assert_bigdecimal_similar(expected, actual)
    assert_equal BigDecimal.new(expected.to_s), BigDecimal.new(actual.to_s)
    assert_equal Java::JavaMath::BigDecimal, actual.class
  end

end
