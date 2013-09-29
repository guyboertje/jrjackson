$LOAD_PATH << File.expand_path('../../lib', __FILE__)

require 'test/unit'
require 'bigdecimal'
require 'jrjackson'

class BigDecimalTest < Test::Unit::TestCase

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
  end

end
