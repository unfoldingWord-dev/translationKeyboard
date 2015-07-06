# app/views/api/v2/keyboard/index.json.rabl

object false

node(:updated_at) { |m|  @updated_at_epoch }

child @keyboards, :object_root => false do
  attributes :id, :iso_language, :iso_region, :language_name
  attributes :updated_at_epoch => :updated_at
end
