# == Schema Information
#
# Table name: keyboards
#
#  id         :integer          not null, primary key
#  name       :string(255)
#  created_at :datetime
#  updated_at :datetime
#

class Keyboard < ActiveRecord::Base
	has_many :keyboard_variants
end
