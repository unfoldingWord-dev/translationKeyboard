# app/views/api/v3/keyboard/index.json.rabl

object @a_keyboard
attributes :id => :k_id, :name => :n, :created_at_epoch => :c_at, :updated_at_epoch => :u_at
attributes :iso_region => :iso_r, :iso_language => :iso_l
child :keyboard_variants, :object_root => false do
  attributes :name => :n
  attributes :created_at_epoch => :c_at, :updated_at_epoch => :u_at
  node :key_position_rows do |row|
    row.key_position_rows.map { |c|

        { :key_position_columns => partial("api/v3/keyboard/_key_pos", :object => c) }
    }

  end
end