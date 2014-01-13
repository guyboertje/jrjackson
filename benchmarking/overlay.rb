module Overlay

  JsonTokenUnset         = 0   # {
  JsonCurlyBracketLeft   = 1   # {
  JsonCurlyBracketRight  = 2   # }
  JsonSquareBracketLeft  = 3   # [
  JsonSquareBracketRight = 4   # ]
  JsonColon              = 5   # :
  JsonComma              = 6   # the , between properties
  JsonStringToken        = 7   # "
  JsonNumberToken        = 8   #
  JsonFloatToken         = 9   #
  JsonUnquotedToken      = 10  #

  Delims = '#{}[]:,'

  ParseError = Class.new(StandardError)

  class Tokeniser
    attr_reader :position, :size, :kind
    def initialize(string)
      @str = string
      @position = 0
      @size = 0
      @kind = 0
      @length = string.size
      parse
    end

    def parse_next
      @position += @size
      parse
      @kind
    end

    def parse
      skip_ws
      @size = 0
      char = @str[@position]
      found = false
      1.upto(6) do |i|
        if char == Delims[i]
          found, @size, @kind = true, 1, i
          break
        end
      end

      if !found
        case char
        when '"'
          parse_string
        when ?0, ?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?-, ?+
          parse_number
        else
          parse_unquoted
        end
      end
    end

    def parse_number
      @size = 1
      eon = false
      @kind = JsonNumberToken
      while(!eon) do
        case @str[@position + @size]
        when ?0, ?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9
          @size = @size.next
        when ?., ?E
          @kind = JsonFloatToken
          @size = @size.next
        else
          eon = true
        end
      end
    end

    def parse_string
      @size = 0
      until @str[@position + @size.next] == '"' &&
          @str[@position + @size] != ?\ do
        @size = @size.next
      end
      @kind = JsonStringToken
      @size += 2 
    end

    def parse_unquoted
      @size = 1
      while !Delims.index(@str[@position + @size])
        @size = @size.next
      end
      @kind = JsonUnquotedToken
    end

    def token_extent
      [@position, @size]
    end

    def token_string_extent
      [@position.next, @size - 2]
    end

    def more?
      @position + @size < @length
    end

    def advance
      @position += @size
    end

    def skip_ws
      ws = true
      while(ws) do
        case @str[@position]
        when ' ', "\t", "\n", "\r"
          @position = @position.next
        else
          ws = false
        end
      end
    end
  end

  class Parser
    attr_reader :tokeniser

    def parse(string)
      @source = string
      @tokeniser = Tokeniser.new(@source)
      # parse_object || parse_array
      parse_bm
    end

    def build_hash
      @result[@depth].each_slice(2).each_with_object({}) {|(k,v),h| h[k] = v}
    end

    def pluck_string
      @source[*@tokeniser.token_string_extent]
    end

    def pluck_unquoted
      value = @source[*@tokeniser.token_extent]
      dcv = value.downcase
      return true if dcv == 'true'
      return false if dcv == 'false'
      return nil if dcv == 'null'
      # raise exception? 
      value
    end

    def pluck_number
      @source[*@tokeniser.token_extent].to_i
    end

    def pluck_float
      @source[*@tokeniser.token_extent].to_f
    end

    def parse_bm
      while @tokeniser.more?
        @tokeniser.parse_next
      end
      "bench 1"
    end

    def parse_object
      return if !@tokeniser.more? || @tokeniser.kind != JsonCurlyBracketLeft
      
      object = Hash.new
      @tokeniser.parse_next
      
      while @tokeniser.kind != JsonCurlyBracketRight
        raise_unless_kind JsonStringToken

        key = pluck_string
        @tokeniser.parse_next
        raise_unless_kind JsonColon

        object[key] = case @tokeniser.parse_next
              when JsonStringToken
                pluck_string
              when JsonNumberToken
                pluck_number
              when JsonFloatToken
                pluck_float
              when JsonCurlyBracketLeft
                parse_object
              when JsonSquareBracketLeft
                parse_array
              when JsonUnquotedToken
                pluck_unquoted
              else
                raise ParseError.new "Token mismatch: expected an object value at position: #{@tokeniser.position}"
              end

        if @tokeniser.parse_next == JsonComma
          @tokeniser.parse_next
        end
      end

      object
    end

    def parse_array
      return if !@tokeniser.more? || @tokeniser.kind != JsonSquareBracketLeft
      
      object = []
      @tokeniser.parse_next

      while @tokeniser.kind != JsonSquareBracketRight
        object.push case @tokeniser.kind
              when JsonStringToken
                pluck_string
              when JsonNumberToken
                pluck_number
              when JsonFloatToken
                pluck_float
              when JsonCurlyBracketLeft
                parse_object
              when JsonSquareBracketLeft
                parse_array
              when JsonUnquotedToken
                pluck_unquoted
              else
                raise ParseError.new "Token mismatch: expected an array value at position: #{@tokeniser.position}"
              end

        if @tokeniser.parse_next == JsonComma
          @tokeniser.parse_next
        end
      end

      object
    end

    def raise_unless_kind(kind)
      if @tokeniser.kind != kind
        raise ParseError.new "Token mismatch: expected #{kind} but got: #{@tokeniser.kind}"
      end
    end
  end
end

# require 'awesome_print'


# parser = Overlay::Parser.new
# js = '[{"a":42,"b":"str","c":{"d":11,"e":12}}]'
# ap parser.parse(js)
