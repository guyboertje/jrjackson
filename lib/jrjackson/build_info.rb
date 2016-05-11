module JrJackson
  module BuildInfo
    def self.version
      '0.4.0'
    end

    def self.release_date
      '2016-05-11'
    end

    def self.files
      `git ls-files`.split($/).select{|f| f !~ /\Abenchmarking/}
    end
  end
end
