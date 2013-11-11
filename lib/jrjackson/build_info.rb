module JrJackson
  module BuildInfo
    def self.version
      '0.2.5'
    end

    def self.files
      `git ls-files`.split($/).select{|f| f !~ /\Abenchmarking/}
    end
  end
end
