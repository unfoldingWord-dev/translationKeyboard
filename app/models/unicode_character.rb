
class UnicodeCharacter < ActiveRecord::Base
  has_many :characters
  has_many :character_unicode_mapping

end
